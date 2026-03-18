package com.emedicalbooking.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePackageRequest {

    private String name;

    private String typeCode;

    private Integer clinicId;

    private Integer price;

    private String note;

    private String imageBase64;

    private String descriptionHTML;

    private String descriptionMarkdown;

    private List<PackageServiceItemRequest> packageServices;
}
