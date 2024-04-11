package com.nk.schedular.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTOList {
    private List<ScheduleDTO> scheduleDTOs;
	@JsonProperty("total_pages")
	private Integer totalPages;
	private Long total;
	@JsonProperty("sort_order")
	private SortOrder sortOrder;
	@JsonProperty("sort_by")
	private ScheduleShortBy sortBy;
}
