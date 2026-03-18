package com.emedicalbooking.dto.request;

import lombok.Data;

@Data
public class PackageServiceItemRequest {
    private String groupServiceCode; // keyMap của all_codes type GROUP_SERVICE (GS1..GS4)
    private String serviceName;
    private String description;
}
