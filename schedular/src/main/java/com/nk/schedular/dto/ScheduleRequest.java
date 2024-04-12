package com.nk.schedular.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Schedule request object", requiredMode = Schema.RequiredMode.REQUIRED)
public class ScheduleRequest {
    @Schema(description = "schedule id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("schedule_id")
    private String scheduleId;

    @Schema(description = "cron schedule", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("cron_schedule")
    private String cronSchedule;

    @Schema(description = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    private Long id;
    
}
