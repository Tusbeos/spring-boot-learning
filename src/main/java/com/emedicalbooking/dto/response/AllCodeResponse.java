package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllCodeResponse {
    private String keyMap;
    private String valueEn;
    private String valueVi;
}
