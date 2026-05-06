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

    private Long clinicId;
    private String clinicName;

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
                .image(resolveImage(user))
                .roleId(user.getRoleData() != null ? user.getRoleData().getKeyMap() : null)
                .roleVi(user.getRoleData() != null ? user.getRoleData().getValueVi() : null)
                .gender(user.getGenderData() != null ? user.getGenderData().getKeyMap() : null)
                .genderVi(user.getGenderData() != null ? user.getGenderData().getValueVi() : null)
                .positionId(user.getPositionData() != null ? user.getPositionData().getKeyMap() : null)
                .positionVi(user.getPositionData() != null ? user.getPositionData().getValueVi() : null)
                .clinicId(user.getClinic() != null ? user.getClinic().getId() : null)
                .clinicName(user.getClinic() != null ? user.getClinic().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private static String resolveImage(User user) {
        byte[] image = user.getImage();
        if (image == null && user.getClinic() != null) {
            image = user.getClinic().getImage();
        }
        return image != null ? Base64.getEncoder().encodeToString(image) : null;
    }
}
