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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleDTOList;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.enums.ScheduleShortBy;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.service.ScheduleService;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService mockScheduleService;

    private ObjectMapper objectMapper;

    private ScheduleDTOList scheduleList;

    private ScheduleDTO scheduleDTO;
    private WebResponse <ScheduleDTOList> webResponse;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        scheduleDTO = ScheduleDTO.builder().id(1L).scheduleId("scheduleId").cronSchedule("cronSchedule").build();
        scheduleList = ScheduleDTOList.builder()
                        .scheduleDTOs(List.of(scheduleDTO))
                        .totalPages(2)
                        .sortBy(ScheduleShortBy.SCHEDULE_ID)
                        .sortOrder(SortOrder.ASC).build();
        webResponse = WebResponse.<ScheduleDTOList>builder().data(scheduleList).build();
            
    }

    @Test
    void testSaveSchedule() throws Exception {
        // Setup
        when(mockScheduleService.saveSchedule(ScheduleRequest.builder().build()))
                .thenReturn(scheduleDTO);
        WebResponse<ScheduleDTO> webResponse = new WebResponse<>(scheduleDTO,"Saved Successfully !!",null);
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/api/schedule")
                        .content(objectMapper.writeValueAsString(ScheduleRequest.builder().build())).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testUpdateSchedule() throws Exception {
        // Setup
        ScheduleRequest scheduleRequest = ScheduleRequest.builder().build();
        when(mockScheduleService.updateSchedule(scheduleRequest))
                .thenReturn(scheduleDTO);
        WebResponse<ScheduleDTO> webResponse = new WebResponse<>(scheduleDTO,"Updated Successfully !!",null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(put("/api/schedule")
                        .content(objectMapper.writeValueAsString(scheduleRequest)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testGetSchedule() throws Exception {
        // Setup
        when(mockScheduleService.getSchedule("scheduleId")).thenReturn(scheduleDTO);
        WebResponse<ScheduleDTO> webResponse = new WebResponse<>(scheduleDTO,"Fetched Successfully !!",null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/schedule")
                        .param("schedule_id", "scheduleId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testGetAllSchedule() throws Exception {
        // Setup
        when(mockScheduleService.getAllSchedules(0, 0, SortOrder.ASC, ScheduleShortBy.SCHEDULE_ID))
                .thenReturn(scheduleList);
        webResponse.setMessage("Fetched Successfully !!");
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/schedule/all")
                        .param("page", "0")
                        .param("page_size", "0")
                        .param("sort_order", "ASC")
                        .param("sort_by", "SCHEDULE_ID")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testGetAllSchedulesWithCronExpration() throws Exception {
        // Setup
        when(mockScheduleService.getAllSchedulesByCronSchedule(0, 0, SortOrder.ASC, ScheduleShortBy.SCHEDULE_ID,
                "cronExpression")).thenReturn(scheduleList);
        webResponse.setMessage("Fetched Successfully !!");
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/schedule/with-cron-expression")
                        .param("cron_expression", "cronExpression")
                        .param("page", "0")
                        .param("page_size", "0")
                        .param("sort_order", "ASC")
                        .param("sort_by", "SCHEDULE_ID")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(webResponse));
    }

    @Test
    void testDeleteSchedule() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/api/schedule")
                        .param("schedule_id", "scheduleId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"data\":true,\"message\":\"Deleted Successfully !!\",\"errors\":null}");
        verify(mockScheduleService).deleteSchedule("scheduleId");
    }
}
