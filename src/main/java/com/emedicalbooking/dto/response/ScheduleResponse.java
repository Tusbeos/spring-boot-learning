package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private Long id;
    private Long doctorId;
    private String date;
    private String timeType;
    private int maxNumber;
    private int currentNumber;
    private AllCodeResponse timeTypeData;
}
