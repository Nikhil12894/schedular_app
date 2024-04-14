package com.nk.schedular.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.constants.Testconstants;
import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.TaskDTO;
import com.nk.schedular.dto.TaskList;
import com.nk.schedular.dto.TaskRequest;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.enums.TaskShortBy;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.DuplicateTransactionException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.exception.NotFoundException;
import com.nk.schedular.model.DemoTask;
import com.nk.schedular.model.Schedule;
import com.nk.schedular.repo.TaskRepo;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
        @Mock
        private TaskRepo mockTaskRepo;
        @Mock
        private ScheduleService mockScheduleService;

        private TaskService taskServiceUnderTest;
        private TaskRequest task;
        private TaskRequest taskWithNullID;
        private TaskRequest taskWithPstvID;
        private TaskDTO expectedResult;
        private TaskList nullTaskListExpectedResult;
        private TaskList taskListExpectedResult;

        @BeforeEach
        void setUp() {
                taskServiceUnderTest = new TaskService(mockTaskRepo, mockScheduleService);

                // Setup
                task = TaskRequest.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(ScheduleRequest.builder().build())
                                .build();

                taskWithNullID = TaskRequest.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(ScheduleRequest.builder().build())
                                .build();
                taskWithPstvID = TaskRequest.builder()
                                .id(1L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(ScheduleRequest.builder().build())
                                .build();
                expectedResult = TaskDTO.builder()
                                .id(0L)
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(ScheduleDTO.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                nullTaskListExpectedResult = TaskList.builder()
                                .tasks(Collections.emptyList())
                                .totalPages(null)
                                .total(null)
                                .sortOrder(null)
                                .sortBy(null)
                                .build();

                taskListExpectedResult = TaskList.builder()
                                .tasks(List.of(TaskDTO.builder()
                                                .id(0L)
                                                .description("description")
                                                .isSchedularEnabled(false)
                                                .schedule(ScheduleDTO.builder()
                                                                .createdBy(0L)
                                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                                .lastUpdatedBy(0L)
                                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                                .build())
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build()))
                                .totalPages(1)
                                .total(1L)
                                .sortOrder(SortOrder.ASC)
                                .sortBy(TaskShortBy.NAME)
                                .build();

        }

        @Test
        void testSaveTask() {
                // Arrange
                when(mockTaskRepo.existsByTaskId("taskId")).thenReturn(false, false);
                final Schedule schedule = Schedule.builder()
                                .createdBy(0L)
                                .scheduleId("scheduleId")
                                .cronSchedule("cronSchedule")
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.findOrCreateSchedule(any())).thenReturn(schedule);
                 // Configure ScheduleService.mapScheduleToDTO(...).
                 final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                 .createdBy(0L)
                                 .createdAt(Testconstants.DEFAULT_DATETIME)
                                 .scheduleId("scheduleId")
                                 .cronSchedule("cronSchedule")
                                 .lastUpdatedBy(0L)
                                 .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                 .build();
                 when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                 .scheduleId("scheduleId")
                                 .cronSchedule("cronSchedule")
                                 .createdBy(0L)
                                 .createdAt(Testconstants.DEFAULT_DATETIME)
                                 .lastUpdatedBy(0L)
                                 .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                 .build())).thenReturn(scheduleDTO);
                // Act
                final TaskDTO result = taskServiceUnderTest.saveTask(task);

                // Assert
                assertTaskDTO(result, expectedResult);
        }

        private void assertTaskDTO(TaskDTO result, TaskDTO expectedResult) {
                assertThat(result.getId()).isEqualTo(expectedResult.getId());
                assertThat(result.getDescription()).isEqualTo(expectedResult.getDescription());
                assertThat(result.getIsSchedularEnabled()).isEqualTo(expectedResult.getIsSchedularEnabled());
                assertScheduleDTO(result.getSchedule(), expectedResult.getSchedule());
                assertThat(result.getCreatedBy()).isEqualTo(expectedResult.getCreatedBy());
                assertThat(result.getCreatedAt()).isNotNull();
                assertThat(result.getLastUpdatedBy()).isEqualTo(expectedResult.getLastUpdatedBy());
                assertThat(result.getLastUpdatedAt()).isNotNull();
        }

        private void assertScheduleDTO(ScheduleDTO result, ScheduleDTO expectedResult) {
                assertThat(result.getCreatedBy()).isEqualTo(expectedResult.getCreatedBy());
                assertThat(result.getCreatedAt()).isEqualTo(expectedResult.getCreatedAt());
                assertThat(result.getLastUpdatedBy()).isEqualTo(expectedResult.getLastUpdatedBy());
                assertThat(result.getLastUpdatedAt()).isEqualTo(expectedResult.getLastUpdatedAt());
        }

        @Test
        void testSaveTask_WithNullID() {
                // Setup
                when(mockTaskRepo.existsByTaskId("taskId")).thenReturn(false);

                // Configure ScheduleService.findOrCreateSchedule(...).
                final Schedule schedule = Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.findOrCreateSchedule(ScheduleRequest.builder().build())).thenReturn(schedule);

                // Configure TaskRepo.save(...).
                when(mockTaskRepo.existsByTaskId("taskId")).thenReturn(false);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskDTO result = taskServiceUnderTest.saveTask(taskWithNullID);

                // Verify the results
                assertThat(result.getDescription()).isEqualTo(expectedResult.getDescription());
                assertThat(result.getIsSchedularEnabled()).isEqualTo(expectedResult.getIsSchedularEnabled());
                assertThat(result.getSchedule()).isEqualTo(expectedResult.getSchedule());
                assertThat(result.getCreatedBy()).isEqualTo(expectedResult.getCreatedBy());
                assertThat(result.getCreatedAt()).isNotNull();
                assertThat(result.getLastUpdatedBy()).isEqualTo(expectedResult.getLastUpdatedBy());
                assertThat(result.getLastUpdatedAt()).isNotNull();
        }

        @Test
        void testSaveTask_TaskIdAlreadyExists() {
                // Configure TaskRepo.save(...).
                when(mockTaskRepo.existsByTaskId("taskId")).thenReturn(true);

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.saveTask(task))
                                .isInstanceOf(DuplicateTransactionException.class);
        }

        @Test
        void testGetTask() {

                // Configure TaskRepo.findByTaskId(...).
                final Optional<DemoTask> demoTask = Optional.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build());
                when(mockTaskRepo.findByTaskId("taskId")).thenReturn(demoTask);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskDTO result = taskServiceUnderTest.getTask("taskId");

                // Verify the results
                assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void testGetTask_TaskRepoReturnsAbsent() {
                // Setup
                when(mockTaskRepo.findByTaskId("taskId")).thenReturn(Optional.empty());

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTask("taskId"))
                                .isInstanceOf(BadRequestException.class);
        }

        @Test
        void testUpdateTask() {
                // Setup
                final TaskDTO expectedResult = TaskDTO.builder()
                                .id(1L)
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(ScheduleDTO.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();

                // Configure ScheduleService.findOrCreateSchedule(...).
                final Schedule schedule = Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.findOrCreateSchedule(ScheduleRequest.builder().build())).thenReturn(schedule);

                // Configure TaskRepo.save(...).
                when(mockTaskRepo.existsById(1L)).thenReturn(true);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskDTO result = taskServiceUnderTest.updateTask(taskWithPstvID);

                // Verify the results
                assertThat(result.getId()).isEqualTo(expectedResult.getId());
                assertThat(result.getDescription()).isEqualTo(expectedResult.getDescription());
                assertThat(result.getIsSchedularEnabled()).isEqualTo(expectedResult.getIsSchedularEnabled());
                assertThat(result.getSchedule()).isEqualTo(expectedResult.getSchedule());
                assertThat(result.getCreatedBy()).isNull();
                assertThat(result.getCreatedAt()).isNull();
                assertThat(result.getLastUpdatedBy()).isEqualTo(expectedResult.getLastUpdatedBy());
                assertThat(result.getLastUpdatedAt()).isNotNull();
        }

        @Test
        void testUpdateTask_Task_ID_NOT_FOUND() {
                // Configure TaskRepo.save(...).
                when(mockTaskRepo.existsById(1L)).thenReturn(false);

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.updateTask(taskWithPstvID))
                                .isInstanceOf(NotFoundException.class)
                                .hasMessage(ApiConstants.INVALID_TASK_ID + " " + taskWithPstvID.getId());
        }

        @Test
        void testDeleteTask1() {
                // Setup
                // Run the test
                taskServiceUnderTest.deleteTask(0L);

                // Verify the results
                verify(mockTaskRepo).deleteById(0L);
        }

        @Test
        void testDeleteTask2() {
                // Setup
                // Run the test
                taskServiceUnderTest.deleteTask("taskId");

                // Verify the results
                verify(mockTaskRepo).deleteByTaskId("taskId");
        }

        @Test
        void testDeleteTasks() {
                // Setup
                // Run the test
                taskServiceUnderTest.deleteTasks(List.of("value"));

                // Verify the results
                verify(mockTaskRepo).deleteByTaskIds(List.of("value"));
        }

        @Test
        void testDeleteTasksWithIds() {
                // Setup
                // Run the test
                taskServiceUnderTest.deleteTasksWithIds(List.of(0L));

                // Verify the results
                verify(mockTaskRepo).deleteByIds(List.of(0L));
        }

        @Test
        void testGetTaskByTaskIds() {

                // Configure TaskRepo.findAllTaskWithTaskIds(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(List.of("value"))))
                                .thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getTaskByTaskIds(List.of("value"),
                                ApiConstants.DEFAULT_PAGE, ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                                TaskShortBy.NAME);

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetTaskByTaskIds_pageIsNull() {

                // Configure TaskRepo.findAllTaskWithTaskIds(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(List.of("value"))))
                                .thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getTaskByTaskIds(List.of("value"), null,
                                ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                                TaskShortBy.NAME);

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetTaskByTaskIds_pageSizeIsNull() {

                // Configure TaskRepo.findAllTaskWithTaskIds(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(List.of("value"))))
                                .thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getTaskByTaskIds(List.of("value"),
                                ApiConstants.DEFAULT_PAGE, null, SortOrder.ASC,
                                TaskShortBy.NAME);

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetTaskByTaskIds_TaskRepoReturnsNoItems() {
                // Setup
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(List.of("value"))))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));

                // Configure ScheduleService.mapScheduleToDTO(...).

                // Run the test
                final TaskList result = taskServiceUnderTest.getTaskByTaskIds(List.of("value"), null, null,
                                SortOrder.NONE,
                                TaskShortBy.NONE);

                // Verify the results
                assertThat(result).isEqualTo(nullTaskListExpectedResult);
        }

        @Test
        void testGetTaskByTaskIds_ShortOrderIsNull() {
                // Setup
                List<String> taskIds = List.of("value");
                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTaskByTaskIds(taskIds, null, null, null,
                                TaskShortBy.NAME)).isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("nvalid sort parameter: null. Must be 'ASC' or 'DESC'.");

        }

        @Test
        void testGetTaskByTaskIds_ShortByIsNull() {
                // Setup
                List<String> taskIds = List.of("value");
                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTaskByTaskIds(taskIds, null, null, SortOrder.ASC,
                                null)).isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Invalid sortBy parameter: null. Must be a valid field name.");

        }

        @Test
        void testGetTaskByTaskIds_ShortByAndShortOrderIsNull() {
                // Setup
                List<String> taskIds = List.of("value");
                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTaskByTaskIds(taskIds, null, null, null,
                                null)).isInstanceOf(BadRequestException.class);

        }

        @Test
        void testGetTaskByTaskIds_PageIsInvalid() {
                // Setup
                List<String> taskIds = List.of("value");
                // Configure ScheduleService.mapScheduleToDTO(...).
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(taskIds)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTaskByTaskIds(taskIds, 2,
                                ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.DESC,
                                TaskShortBy.NAME)).isInstanceOf(BadRequestException.class);

        }

        @Test
        void testGetTaskByTaskIds_findAllTaskWithTaskIds_ReturnsNull() {
                // Setup
                List<String> taskIds = List.of("value");
                // Configure ScheduleService.mapScheduleToDTO(...).
                when(mockTaskRepo.findAllTaskWithTaskIds(any(Pageable.class), eq(taskIds)))
                                .thenReturn(null);

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getTaskByTaskIds(taskIds, 2,
                                ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                                TaskShortBy.NAME)).isInstanceOf(InternalServerException.class);
        }

        @Test
        void testGetAllTask() {

                // Configure TaskRepo.findAll(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAll(any(Pageable.class))).thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTask(ApiConstants.DEFAULT_PAGE,
                                ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC, TaskShortBy.NAME);

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetAllTask_TaskRepoReturnsNoItems() {
                // Setup
                when(mockTaskRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

                // Configure ScheduleService.mapScheduleToDTO(...).
                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTask(null, 1, SortOrder.ASC, TaskShortBy.NAME);

                // Verify the results
                assertThat(result).isEqualTo(nullTaskListExpectedResult);
        }

        @Test
        void testGetAllTask_findAll_ReturnsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).
                when(mockTaskRepo.findAll(any(Pageable.class)))
                                .thenReturn(null);

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTask(2, ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                                                TaskShortBy.NAME))
                                .isInstanceOf(InternalServerException.class);
        }

        @Test
        void testGetAllTask_ShortOrderIsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTask(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                TaskShortBy.NAME)).isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTask_ShortByIsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTask(2, ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                                                null))
                                .isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTask_ShortByAndShortOrderIsNull() {
                // Setup

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTask(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                null)).isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskBySchedule() {

                // Configure TaskRepo.findAll(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAll(any(Pageable.class), eq("scheduleId"))).thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTaskBySchedule(1, null, SortOrder.ASC,
                                TaskShortBy.NAME,
                                "scheduleId");

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetAllTaskBySchedule_TaskRepoReturnsNoItems() {
                // Setup
                when(mockTaskRepo.findAll(any(Pageable.class), eq("scheduleId")))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));

                // Configure ScheduleService.mapScheduleToDTO(...).

                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTaskBySchedule(1, 1, SortOrder.ASC, TaskShortBy.NAME,
                                "scheduleId");

                // Verify the results
                assertThat(result).isEqualTo(nullTaskListExpectedResult);
        }

        @Test
        void testGetAllTaskBySchedule_findAll_ReturnsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).
                when(mockTaskRepo.findAll(any(Pageable.class), eq("scheduleId")))
                                .thenReturn(null);

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTaskBySchedule(2, ApiConstants.DEFAULT_PAGE_SIZE,
                                SortOrder.ASC,
                                TaskShortBy.NAME, "scheduleId")).isInstanceOf(InternalServerException.class);
        }

        @Test
        void testGetAllTaskBySchedule_ShortOrderIsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTaskBySchedule(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                                TaskShortBy.NAME, "scheduleId"))
                                .isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskBySchedule_ShortByIsNull() {
                // Setup
                // Configure ScheduleService.findAll(...).

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTaskBySchedule(2, ApiConstants.DEFAULT_PAGE_SIZE,
                                SortOrder.ASC,
                                null, "scheduleId")).isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskBySchedule_ShortByAndShortOrderIsNull() {
                // Setup

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTaskBySchedule(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                                null, "scheduleId"))
                                .isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskByCronExp() {

                // Configure TaskRepo.findAllWithCron(...).
                final Page<DemoTask> demoTasks = new PageImpl<>(List.of(DemoTask.builder()
                                .id(0L)
                                .taskId("taskId")
                                .description("description")
                                .isSchedularEnabled(false)
                                .schedule(Schedule.builder()
                                                .createdBy(0L)
                                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                                .lastUpdatedBy(0L)
                                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                                .build())
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build()));
                when(mockTaskRepo.findAllWithCron(any(Pageable.class), eq("cronSchedule"))).thenReturn(demoTasks);

                // Configure ScheduleService.mapScheduleToDTO(...).
                final ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build();
                when(mockScheduleService.mapScheduleToDTO(Schedule.builder()
                                .createdBy(0L)
                                .createdAt(Testconstants.DEFAULT_DATETIME)
                                .lastUpdatedBy(0L)
                                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                                .build())).thenReturn(scheduleDTO);

                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTaskByCronExp(ApiConstants.DEFAULT_PAGE,
                                ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC, TaskShortBy.NAME,
                                "cronSchedule");

                // Verify the results
                assertThat(result).isEqualTo(taskListExpectedResult);
        }

        @Test
        void testGetAllTaskByCronExp_TaskRepoReturnsNoItems() {
                // Setup
                when(mockTaskRepo.findAllWithCron(any(Pageable.class), eq("cronSchedule")))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));

                // Configure ScheduleService.mapScheduleToDTO(...).

                // Run the test
                final TaskList result = taskServiceUnderTest.getAllTaskByCronExp(1, 1, SortOrder.ASC, TaskShortBy.NAME,
                                "cronSchedule");

                // Verify the results
                assertThat(result).isEqualTo(nullTaskListExpectedResult);
        }

        @Test
        void testGetAllTaskByCronExp_findAllWithCron_ReturnsNull() {
                // Setup
                // Configure mockTaskRepo.findAllWithCron(...).
                when(mockTaskRepo.findAllWithCron(any(Pageable.class), eq("cronSchedule")))
                                .thenReturn(null);

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTaskByCronExp(2, ApiConstants.DEFAULT_PAGE_SIZE,
                                SortOrder.ASC,
                                TaskShortBy.NAME, "cronSchedule")).isInstanceOf(InternalServerException.class);
        }

        @Test
        void testGetAllTaskByCronExp_ShortOrderIsNull() {
                // Setup

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTaskByCronExp(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                                TaskShortBy.NAME, "cronSchedule"))
                                .isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskByCronExp_ShortByIsNull() {
                // Setup

                // Run the test
                assertThatThrownBy(() -> taskServiceUnderTest.getAllTaskByCronExp(2, ApiConstants.DEFAULT_PAGE_SIZE,
                                SortOrder.ASC,
                                null, "cronSchedule")).isInstanceOf(BadRequestException.class);
        }

        @Test
        void testGetAllTaskByCronExp_ShortByAndShortOrderIsNull() {
                // Setup

                // Run the test
                assertThatThrownBy(
                                () -> taskServiceUnderTest.getAllTaskByCronExp(2, ApiConstants.DEFAULT_PAGE_SIZE, null,
                                                null, "cronSchedule"))
                                .isInstanceOf(BadRequestException.class);
        }
}
