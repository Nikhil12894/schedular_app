package com.nk.schedular.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nk.schedular.constants.DateConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseDTO {
    @JsonProperty("created_by")
    private Long createdBy;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "creation date",accessMode = Schema.AccessMode.READ_ONLY,type = "string",pattern= DateConstants.DATE_TIME_PATTERN_REGX,example = "2023-06-08 10:45:00")
    @JsonProperty("creation_date")
    private LocalDateTime createdAt;

    @JsonProperty("last_updated_by")
    private Long lastUpdatedBy;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "last update date",accessMode = Schema.AccessMode.READ_ONLY,type = "string",pattern= DateConstants.DATE_TIME_PATTERN_REGX,example = "2023-06-08 10:45:00")
    @JsonProperty("last_update_date")
    private LocalDateTime lastUpdatedAt;
}
