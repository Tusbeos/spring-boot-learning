package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Integer id;
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private String roleId;
}
