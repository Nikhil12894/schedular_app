package com.nk.schedular.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.coreoz.wisp.JobStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.TaskDTO;
import com.nk.schedular.dto.TaskList;
import com.nk.schedular.dto.TaskRequest;
import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.enums.TaskShortBy;
import com.nk.schedular.service.SchedularService;
import com.nk.schedular.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchedularService mockSchedularService;
    
    @MockBean
    private TaskService mockTaskService;

    private ObjectMapper objectMapper;

    private TaskDTO taskDTO;
    private TaskList taskList;
    WebResponse<TaskList> webResponse;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        taskDTO = TaskDTO.builder()
                                .id(1L)
                                .description("description")
                                .isSchedularEnabled(true)
                                .taskId("taskId")
                                .build();
        taskList = TaskList.builder()
        .tasks(List.of(taskDTO)).totalPages(2).sortBy(TaskShortBy.NAME).sortOrder(SortOrder.ASC).total(10L).build();
        webResponse = WebResponse.<TaskList>builder().data(taskList)
        .build();
    }
    @Test
    void testGetMethodName() throws Exception {
        // Setup
        when(mockSchedularService.scheduleTask("taskId")).thenReturn(JobStatus.DONE);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/task/start")
                        .param("task_id", "taskId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":\"DONE\",\"message\":\"Task Started Successfully !!\",\"errors\":null}");
    }

    @Test
    void testGetCancelTask() throws Exception {
        // Setup
        when(mockSchedularService.cancelTask("taskId")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/task/cancel")
                        .param("task_id", "taskId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":false,\"message\":\"Task Canceled Successfully !!\",\"errors\":null}");
    }

    @Test
    void testGetAllTask() throws Exception {
        // Setup
        webResponse.setMessage("Successfully fetched all tasks");
        when(mockTaskService.getAllTask(0, 0, SortOrder.ASC, TaskShortBy.NAME)).thenReturn(taskList);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/task/all")
                        .param("page", "0")
                        .param("page_size", "0")
                        .param("sort_order", "ASC")
                        .param("sort_by", "NAME")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testSaveTask() throws Exception {
        // Setup
        when(mockTaskService.saveTask(TaskRequest.builder().build())).thenReturn(taskDTO);
        WebResponse<TaskDTO> webResponse = new WebResponse<TaskDTO>(taskDTO,"Successfully saved task",null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/api/task")
                        .content(objectMapper.writeValueAsString(TaskRequest.builder().build())).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testUpdateTask() throws Exception {
        // Setup
        when(mockTaskService.updateTask(TaskRequest.builder().build())).thenReturn(taskDTO);
        WebResponse<TaskDTO> webResponse = new WebResponse<TaskDTO>(taskDTO,"Successfully updated task",null);
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/task")
                        .content(objectMapper.writeValueAsString(TaskRequest.builder().build())).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testDeleteTaskWithTaskID() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/api/task")
                        .param("task_id", "taskId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":true,\"message\":\"Deleted Successfully !!\",\"errors\":null}");
        verify(mockTaskService).deleteTask("taskId");
    }

    @Test
    void testDeleteTaskWithID() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/api/task/with-id")
                        .param("task_id", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":true,\"message\":\"Deleted Successfully !!\",\"errors\":null}");
        verify(mockTaskService).deleteTask(0L);
    }

    @Test
    void testDeleteTaskWitsIDs() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/api/task/with-ids")
                        .content(objectMapper.writeValueAsString(List.of(0L))).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":true,\"message\":\"Deleted Successfully !!\",\"errors\":null}");
        verify(mockTaskService).deleteTasksWithIds(List.of(0L));
    }

    @Test
    void testDeleteTaskWithTaskIDs() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/api/task/with-task-ids")
                        .content(objectMapper.writeValueAsString(List.of("value"))).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":true,\"message\":\"Deleted Successfully !!\",\"errors\":null}");
        verify(mockTaskService).deleteTasks(List.of("value"));
    }

    @Test
    void testGetAllTaskWithScheduleID() throws Exception {
        // Setup
        webResponse.setMessage("Successfully fetched all task for given schedule");
        when(mockTaskService.getAllTaskBySchedule(0, 0, SortOrder.ASC, TaskShortBy.NAME, "scheduleId"))
                .thenReturn(taskList);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/task/with-schedule-id")
                        .param("page", "0")
                        .param("page_size", "0")
                        .param("sort_order", "ASC")
                        .param("sort_by", "NAME")
                        .param("schedule_id", "scheduleId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testGetAllTaskWithCronExpression() throws Exception {
        // Setup
        webResponse.setMessage("Successfully fetched all task for given cron expression");
        when(mockTaskService.getAllTaskByCronExp(ApiConstants.DEFAULT_PAGE, ApiConstants.DEFAULT_PAGE_SIZE, 
        SortOrder.ASC, TaskShortBy.NAME, "cronExpression"))
                .thenReturn(taskList);
        
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/task/with-cron-expression")
                        .param("page", ApiConstants.DEFAULT_PAGE.toString())
                        .param("page_size", ApiConstants.DEFAULT_PAGE_SIZE.toString())
                        .param("sort_order", "ASC")
                        .param("sort_by", "NAME")
                        .param("cron_expression", "cronExpression")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }
}
