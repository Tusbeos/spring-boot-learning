package com.emedicalbooking.dto.response;

import com.emedicalbooking.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    // image là byte[] trong entity → trả ra Base64 string cho FE hiển thị
    private String image;

    private String roleId;
    private String roleVi;

    private String gender;
    private String genderVi;

    private String positionId;
    private String positionVi;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .image(user.getImage() != null ? Base64.getEncoder().encodeToString(user.getImage()) : null)
                .roleId(user.getRoleData() != null ? user.getRoleData().getKeyMap() : null)
                .roleVi(user.getRoleData() != null ? user.getRoleData().getValueVi() : null)
                .gender(user.getGenderData() != null ? user.getGenderData().getKeyMap() : null)
                .genderVi(user.getGenderData() != null ? user.getGenderData().getValueVi() : null)
                .positionId(user.getPositionData() != null ? user.getPositionData().getKeyMap() : null)
                .positionVi(user.getPositionData() != null ? user.getPositionData().getValueVi() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
