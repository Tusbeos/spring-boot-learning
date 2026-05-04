package com.emedicalbooking.config;

import com.emedicalbooking.security.UserDetailsServiceImpl;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    // Public read-only endpoints (no auth required)
    private static final String[] PUBLIC_READ_URLS = {
            "/api/all-codes",
            "/api/doctors/top",
            "/api/doctors/{id}",
            "/api/doctors/{id}/schedules",
            "/api/doctors/{id}/services",
            "/api/doctors/{id}/extra-info",
            "/api/specialties",
            "/api/specialties/by-ids",
            "/api/specialties/{id}/doctors",
            "/api/clinics",
            "/api/clinics/{id}",
            "/api/clinics/{id}/doctors",
            "/api/packages",
            "/api/packages/{id}",
            "/api/bookings/verify"
    };

    private static final String ROLE_ADMIN   = com.emedicalbooking.constant.RoleConstants.ADMIN;
    private static final String ROLE_DOCTOR  = com.emedicalbooking.constant.RoleConstants.DOCTOR;
    private static final String ROLE_PATIENT = com.emedicalbooking.constant.RoleConstants.PATIENT;
    private static final String ROLE_CLINIC_MANAGER = com.emedicalbooking.constant.RoleConstants.CLINIC_MANAGER;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, PUBLIC_READ_URLS).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/bookings/verify").permitAll()
                // Đặt lịch không yêu cầu đăng nhập — bệnh nhân có thể đặt với tư cách khách
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/bookings").permitAll()

                // Admin CRUD users + bệnh nhân có thể tự cập nhật thông tin cá nhân
                .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/api/clinic-manager/**").hasRole(ROLE_CLINIC_MANAGER)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/{id}").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/{id}/change-password").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/specialties").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/specialties/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/specialties/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/clinics").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/clinics/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/clinics/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/packages").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/packages/{id}").hasRole(ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/packages/{id}").hasRole(ROLE_ADMIN)

                // Doctor-only: manage own schedules, services, info, confirm bookings
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/doctors/{id}/schedules").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/doctors/{id}/services").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/doctors/{id}/info").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)
                .requestMatchers("/api/doctors/{id}/patients").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/bookings/{id}/confirm").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)

                // Lịch sử khám: bác sĩ tạo/xem, admin xem tất cả, bệnh nhân xem của mình
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/histories").hasAnyRole(ROLE_DOCTOR, ROLE_ADMIN)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/histories/**").authenticated()

                // Hồ sơ bệnh nhân được đặt hộ: chỉ người đã đăng nhập mới được tạo/xem/xoá
                .requestMatchers("/api/patient-profiles/**").authenticated()

                // Còn lại: đã đăng nhập là được
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    /**
     * NimbusJwtDecoder dùng cùng secret key với JwtTokenProvider để verify chữ ký.
     * Spring Security OAuth2 Resource Server tự động:
     *   - Đọc Authorization: Bearer <token>
     *   - Xác thực chữ ký, kiểm tra expiry
     *   - Set SecurityContextHolder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            String email = jwt.getSubject();
            
            // Lấy roles từ JWT (bỏ query DB)
            String rolesStr = jwt.getClaimAsString("roles");
            List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = java.util.Collections.emptyList();
            
            if (rolesStr != null && !rolesStr.isEmpty()) {
                authorities = java.util.Arrays.stream(rolesStr.split(","))
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList();
            }
            
            return new JwtAuthenticationToken(jwt, authorities, email);
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
