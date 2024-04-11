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
@Schema(description = "Update task request object", requiredMode = Schema.RequiredMode.REQUIRED)
public class UpdateTaskRequest {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "task id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("task_id")
    private String taskId;

    @Schema(description = "task description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    private String description;

    @Schema(description = "is schedular enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("is_schedular_enabled")
    private Boolean isSchedularEnabled;

    @Schema(description = "schedule", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("schedule")
    private ScheduleRequest schedule;
}
