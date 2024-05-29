package com.nk.schedular.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO extends BaseDTO{
    private Long id;
    @JsonProperty("task_id")
    private String taskId;
    private String description;
    @JsonProperty("is_schedular_enabled")
    private Boolean isSchedularEnabled;

    private ScheduleDTO schedule;


    public String getSchedule(){
        return this.schedule!=null?this.schedule.getScheduleId():null;
    }
    
}