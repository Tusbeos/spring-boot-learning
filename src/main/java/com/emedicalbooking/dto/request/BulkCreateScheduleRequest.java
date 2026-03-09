package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkCreateScheduleRequest {

    @NotNull(message = "date không được để trống")
    private String formattedDate;

    @NotNull(message = "arrSchedule không được để trống")
    private List<ScheduleItem> arrSchedule;

    @Data
    public static class ScheduleItem {
        private String timeType;
        private String date;
    }
}
