package com.emedicalbooking.security;

import com.emedicalbooking.entity.User;
import com.emedicalbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Dùng JOIN FETCH để load roleData cùng lúc, tránh LazyInitializationException
        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        // Lấy keyMap từ AllCode, ví dụ: "R1", "R2", "R3"
        // Spring Security yêu cầu prefix "ROLE_" → "ROLE_R1", "ROLE_R2", "ROLE_R3"
        String role = user.getRoleData() != null
                ? "ROLE_" + user.getRoleData().getKeyMap()   // ROLE_R1, ROLE_R2, ROLE_R3
                : "ROLE_R3";                                  // mặc định là patient nếu chưa có role

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
