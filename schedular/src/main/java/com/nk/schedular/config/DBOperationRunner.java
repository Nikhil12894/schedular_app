package com.nk.schedular.config;


import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    
    /**
     * Override method to run a specific schedule.
     *
     * @param  args    the arguments passed to the function
     * @return         void
     */
    @Override
    public void run(String... args) {
        setupInitialData();
    }

    private void setupInitialData(){
        ScheduleRequest schedule = ScheduleRequest.builder().scheduleId("EVERY_10_SECONDS").cronSchedule("0/10 * * ? * *").build();
        ScheduleDTO savedSchedule = scheduleService.saveSchedule(schedule);
        // 2. convert JSON array to List
        String jsonArray = "[\n" +
                        "  {\"cron_schedule\": \"*/5 * * * * *\" , \"schedule_id\": \"schedule1\" },\n" + //
                        "  {\"cron_schedule\": \"*/35 * * * * *\", \"schedule_id\": \"schedule2\" },\n" + //
                        "  {\"cron_schedule\": \"*/15 * * * * *\", \"schedule_id\": \"schedule3\" },\n" + //
                        "  {\"cron_schedule\": \"*/20 * * * * *\", \"schedule_id\": \"schedule4\" },\n" + //
                        "  {\"cron_schedule\": \"*/25 * * * * *\", \"schedule_id\": \"schedule5\" },\n" + //
                        "  {\"cron_schedule\": \"*/30 * * * * *\", \"schedule_id\": \"schedule6\" },\n" + //
                        "  {\"cron_schedule\": \"*/35 * * * * *\", \"schedule_id\": \"schedule7\" },\n" + //
                        "  {\"cron_schedule\": \"*/40 * * * * *\", \"schedule_id\": \"schedule8\" },\n" + //
                        "  {\"cron_schedule\": \"*/45 * * * * *\", \"schedule_id\": \"schedule9\" },\n" + //
                        "  {\"cron_schedule\": \"*/50 * * * * *\", \"schedule_id\": \"schedule10\"},\n" + //
                        "  {\"cron_schedule\": \"*/55 * * * * *\", \"schedule_id\": \"schedule11\"},\n" + //
                        "  {\"cron_schedule\": \"*/60 * * * * *\", \"schedule_id\": \"schedule12\"},\n" + //
                        "  {\"cron_schedule\": \"*/13 * * * * *\", \"schedule_id\": \"schedule13\"},\n" + //
                        "  {\"cron_schedule\": \"*/14 * * * * *\", \"schedule_id\": \"schedule14\"},\n" + //
                        "  {\"cron_schedule\": \"*/15 * * * * *\", \"schedule_id\": \"schedule15\"}\n" + //
                        "]";

        try {
            List<ScheduleRequest> person2 = objectMapper.readValue(jsonArray, new TypeReference<List<ScheduleRequest>>() {
            });
            for (ScheduleRequest person : person2) {
                scheduleService.saveSchedule(person);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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