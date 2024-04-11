package com.nk.schedular.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.TaskRequest;
import com.nk.schedular.service.ScheduleService;
import com.nk.schedular.service.TaskService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DBOperationRunner implements CommandLineRunner {

    private final TaskService taskService;
    private final ScheduleService scheduleService;
    
    /**
     * Override method to run a specific schedule.
     *
     * @param  args    the arguments passed to the function
     * @return         void
     */
    @Override
    public void run(String... args) throws Exception {
        ScheduleRequest schedule = ScheduleRequest.builder().scheduleId("EVERY_10_SECONDS").cronSchedule("0/10 * * ? * *").build();
        ScheduleDTO savedSchedule = scheduleService.saveSchedule(schedule);
        taskService.saveTask(
                TaskRequest.builder()
                .taskId("TEST_TASK_1")
                .description("TEST_TASK_1")
                .isSchedularEnabled(true)
                .schedule(ScheduleRequest.builder()
                .id(savedSchedule.getId())
                .scheduleId(savedSchedule.getScheduleId())
                .cronSchedule(savedSchedule.getCronSchedule())
                .build()
                )
                .build());
    }

}