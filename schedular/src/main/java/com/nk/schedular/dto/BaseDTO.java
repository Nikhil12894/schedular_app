package com.nk.schedular.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseDTO {
    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("creation_date")
    private LocalDateTime createdAt;

    @JsonProperty("last_updated_by")
    private Long lastUpdatedBy;

    @JsonProperty("last_update_date")
    private LocalDateTime lastUpdatedAt;
}
