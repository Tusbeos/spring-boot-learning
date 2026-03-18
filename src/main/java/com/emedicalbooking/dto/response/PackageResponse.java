package com.emedicalbooking.dto.response;

import com.emedicalbooking.dto.response.PackageServiceItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private int id;
    private String name;
    private String typeCode;
    private AllCodeResponse typeData;
    private int clinicId;
    private String clinicName;
    private int price;
    private String note;
    private String image; // base64
    private String descriptionHTML;
    private String descriptionMarkdown;
    private List<PackageServiceItemResponse> packageServices;
}
