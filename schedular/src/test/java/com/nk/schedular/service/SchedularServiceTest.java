package com.nk.schedular.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coreoz.wisp.JobStatus;
import com.coreoz.wisp.Scheduler;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.model.DemoTask;
import com.nk.schedular.model.Schedule;
import com.nk.schedular.repo.TaskRepo;

@ExtendWith(MockitoExtension.class)
class SchedularServiceTest {

    @Mock
    private TaskRepo mockTaskRepo;

    private SchedularService schedularServiceUnderTest;

    @BeforeEach
    void setUp() {
        schedularServiceUnderTest = new SchedularService(new Scheduler(1), mockTaskRepo);
    }

    @Test
    void testScheduleTask() {
        // Setup
        // Configure TaskRepo.findByTaskId(...).
        final Optional<DemoTask> demoTask = Optional.of(DemoTask.builder()
                .taskId("taskId")
                .description("description")
                .schedule(Schedule.builder()
                        .scheduleId("EVERY_10_SECONDS").cronSchedule("0/10 * * ? * *")
                        .build())
                .build());
        when(mockTaskRepo.findByTaskId("taskId")).thenReturn(demoTask);

        // Run the test
        final JobStatus result = schedularServiceUnderTest.scheduleTask("taskId");

        // Verify the results
        assertThat(result).isEqualTo(JobStatus.SCHEDULED);
    }

    @Test
    void testScheduleTask_TaskRepoReturnsAbsent() {
        // Setup
        when(mockTaskRepo.findByTaskId("taskId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> schedularServiceUnderTest.scheduleTask("taskId"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testCancelTask_InvalidTaskId() {
        // Setup
        // Run the test
        assertThatThrownBy(() -> schedularServiceUnderTest.cancelTask("invalidTaskId"))
                .isInstanceOf(BadRequestException.class);

    }

    @Test
    void testCancelTask() {
        // Setup
        // Configure TaskRepo.findByTaskId(...).
        final Optional<DemoTask> demoTask = Optional.of(DemoTask.builder()
                .taskId("taskId1")
                .description("description")
                .schedule(Schedule.builder()
                        .scheduleId("EVERY_10_SECONDS").cronSchedule("0/5 * * ? * *")
                        .build())
                .build());
        when(mockTaskRepo.findByTaskId("taskId1")).thenReturn(demoTask);

        // Run the test
        schedularServiceUnderTest.scheduleTask("taskId1");

        Boolean cancelResult = schedularServiceUnderTest.cancelTask("taskId1");
        assertThat(cancelResult).isTrue();

        // // Verify that the task status is updated to cancelled
        // assertThat(demoTask.getStatus()).isEqualTo(TaskStatus.CANCELLED);
    }

}
