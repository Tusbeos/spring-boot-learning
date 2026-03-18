package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageServiceItemResponse {
    private int id;
    private String groupServiceCode;
    private AllCodeResponse groupServiceData;
    private String serviceName;
    private String description;
}
