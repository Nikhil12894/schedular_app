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
import com.nk.schedular.dto.ScheduleDTOList;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.enums.ScheduleShortBy;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.DuplicateTransactionException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.exception.NotFoundException;
import com.nk.schedular.model.Schedule;
import com.nk.schedular.repo.ScheduleRepo;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepo mockScheduleRepo;

    private ScheduleService scheduleServiceUnderTest;
    private ScheduleRequest scheduleRequest;
    private ScheduleDTO expectedResult;
   
    @BeforeEach
    void setUp() {
            scheduleServiceUnderTest = new ScheduleService(mockScheduleRepo);
            scheduleRequest = ScheduleRequest.builder()
                            .scheduleId("scheduleId")
                            .cronSchedule("0/10 * * ? * *")
                            .id(0L)
                            .build();

            expectedResult = ScheduleDTO.builder()
                            .id(0L)
                            .scheduleId("scheduleId")
                            .cronSchedule("0/10 * * ? * *")
                            .createdBy(0L)
                            .createdAt(Testconstants.DEFAULT_DATETIME)
                            .lastUpdatedBy(0L)
                            .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                            .build();
    }

    @Test
    void testSaveSchedule() {
        // Setup

        // Run the test
        final ScheduleDTO result = scheduleServiceUnderTest.saveSchedule(scheduleRequest);

        // Verify the results
        assertThat(result.getCronSchedule()).isEqualTo(expectedResult.getCronSchedule());
        assertThat(result.getScheduleId()).isEqualTo(expectedResult.getScheduleId());
    }

    @Test
    void testSaveSchedule_2() {
        // Setup
        final ScheduleRequest scheduleRequest = ScheduleRequest.builder()
        .scheduleId("scheduleId")
        .cronSchedule("0/10 * * ? * *")
        .id(null)
        .build();
        // Run the test
        final ScheduleDTO result = scheduleServiceUnderTest.saveSchedule(scheduleRequest);

        // Verify the results
        assertThat(result.getCronSchedule()).isEqualTo(expectedResult.getCronSchedule());
        assertThat(result.getScheduleId()).isEqualTo(expectedResult.getScheduleId());
    }

    @Test
    void testSaveSchedule_DuplicateTransaction() {
        // Setup
        // Configure ScheduleRepo.existsByScheduleId(...).
        when(mockScheduleRepo.findByScheduleId("scheduleId")).thenReturn(Optional.of(new Schedule()));

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.saveSchedule(scheduleRequest))
                .isInstanceOf(DuplicateTransactionException.class);
    }

    @Test
    void testSaveSchedule_InvalidCron() {
        // Setup
        final ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleId("scheduleId")
                .cronSchedule("0/4 * * ? * * * *")
                .id(null)
                .build();
        // Configure ScheduleRepo.existsByScheduleId(...).
        // when(mockScheduleRepo.existsByScheduleId("scheduleId")).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.saveSchedule(scheduleRequest))
                .isInstanceOf(BadRequestException.class);
    }


    @Test
    void testUpdateSchedule() {
        // Setup
        final ScheduleRequest updateScheduleRequest = ScheduleRequest.builder()
                .scheduleId("scheduleId")
                .cronSchedule("0/4 * * ? * *")
                .id(1L)
                .build();
        final ScheduleDTO expectedResult = ScheduleDTO.builder()
                .id(1L)
                .scheduleId("scheduleId")
                .cronSchedule("0/4 * * ? * *")
                .createdBy(0L)
                .createdAt(Testconstants.DEFAULT_DATETIME)
                .lastUpdatedBy(0L)
                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                .build();
        // Configure ScheduleRepo.findById(...).
        when(mockScheduleRepo.findById(1L)).thenReturn(Optional.of(new Schedule()));
        // Run the test
        final ScheduleDTO result = scheduleServiceUnderTest.updateSchedule(updateScheduleRequest);

        // Verify the results
        assertThat(result.getId()).isEqualTo(expectedResult.getId());
        assertThat(result.getScheduleId()).isEqualTo(expectedResult.getScheduleId());
        assertThat(result.getCronSchedule()).isEqualTo(expectedResult.getCronSchedule());
        assertThat(result.getLastUpdatedAt()).isNotNull();
        assertThat(result.getLastUpdatedBy()).isEqualTo(expectedResult.getLastUpdatedBy());
    }
    @Test
    void testUpdateSchedule_ScheduleNotFound() {
        // Setup
        final ScheduleRequest updateScheduleRequest = ScheduleRequest.builder()
                .scheduleId("scheduleId")
                .cronSchedule("0/4 * * ? * *")
                .id(1L)
                .build();
        // Configure ScheduleRepo.findById(...).
        when(mockScheduleRepo.findById(1L)).thenReturn(Optional.empty());
        // Run the test
        assertThatThrownBy(() ->scheduleServiceUnderTest.updateSchedule(updateScheduleRequest)).isInstanceOf(NotFoundException.class);

    }


    @Test
    void testDeleteSchedule() {
        // Setup
        // Run the test
        scheduleServiceUnderTest.deleteSchedule("scheduleId");

        // Verify the results
        verify(mockScheduleRepo).deleteByScheduleId("scheduleId");
    }

    @Test
    void testGetSchedule() {
        // Setup
        final ScheduleDTO expectedResult = ScheduleDTO.builder()
                .id(0L)
                .scheduleId("scheduleId")
                .cronSchedule("cronSchedule")
                .createdBy(0L)
                .createdAt(Testconstants.DEFAULT_DATETIME)
                .lastUpdatedBy(0L)
                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                .build();

        // Configure ScheduleRepo.findByScheduleId(...).
        final Optional<Schedule> schedule = Optional.of(Schedule.builder()
                .id(0L)
                .scheduleId("scheduleId")
                .cronSchedule("cronSchedule")
                .createdBy(0L)
                .createdAt(Testconstants.DEFAULT_DATETIME)
                .lastUpdatedBy(0L)
                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                .build());
        when(mockScheduleRepo.findByScheduleId("scheduleId")).thenReturn(schedule);

        // Run the test
        final ScheduleDTO result = scheduleServiceUnderTest.getSchedule("scheduleId");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetSchedule_ScheduleRepoReturnsAbsent() {
        // Setup
        when(mockScheduleRepo.findByScheduleId("scheduleId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getSchedule("scheduleId"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetSchedule_ScheduleIdIsEmptyInRequest() {
        // Setup

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getSchedule(""))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetSchedule_ScheduleIdIsNullInRequest() {
        // Setup

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getSchedule(null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetAllSchedules() {
        // Setup
        final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                .scheduleDTOs(List.of(ScheduleDTO.builder()
                        .id(0L)
                        .scheduleId("scheduleId")
                        .cronSchedule("cronSchedule")
                        .createdBy(0L)
                        .createdAt(Testconstants.DEFAULT_DATETIME)
                        .lastUpdatedBy(0L)
                        .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                        .build()))
                .totalPages(1)
                .total(1L)
                .sortOrder(SortOrder.ASC)
                .sortBy(ScheduleShortBy.SCHEDULE_ID)
                .build();

        // Configure ScheduleRepo.findAll(...).
        final Page<Schedule> schedules = new PageImpl<>(List.of(Schedule.builder()
                .id(0L)
                .scheduleId("scheduleId")
                .cronSchedule("cronSchedule")
                .createdBy(0L)
                .createdAt(Testconstants.DEFAULT_DATETIME)
                .lastUpdatedBy(0L)
                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                .build()));
        when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(schedules);

        // Run the test
        final ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedules(ApiConstants.DEFAULT_PAGE, ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                ScheduleShortBy.SCHEDULE_ID);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedules_ScheduleRepoReturnsNoItems() {
        // Setup
        final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                .scheduleDTOs(Collections.emptyList())
                .totalPages(null)
                .total(null)
                .sortOrder(null)
                .sortBy(null)
                .build();
        when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedules(null, null, SortOrder.NONE,
                ScheduleShortBy.SCHEDULE_ID);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedules_2() {
            // Setup
            when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
            // Run the test
            assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedules(2, ApiConstants.DEFAULT_PAGE_SIZE,
                            SortOrder.ASC,
                            ScheduleShortBy.SCHEDULE_ID))
                            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetAllSchedules_3() {
            // Setup
            // Run the test
            assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedules(2, ApiConstants.DEFAULT_PAGE_SIZE,
                            null,
                            null))
                            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetAllSchedules_4() {
            // Setup

            when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(null);

            // Run the test
            assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedules(ApiConstants.DEFAULT_PAGE,
                            ApiConstants.DEFAULT_PAGE_SIZE,
                            SortOrder.ASC,
                            ScheduleShortBy.SCHEDULE_ID))
                            .isInstanceOf(InternalServerException.class);
    }

    @Test
    void testGetAllSchedules_5() {
            // Setup
            final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                            .scheduleDTOs(Collections.emptyList())
                            .totalPages(null)
                            .total(null)
                            .sortOrder(null)
                            .sortBy(null)
                            .build();
            when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            // Run the test
            ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedules(null,
                            ApiConstants.DEFAULT_PAGE_SIZE,
                            SortOrder.ASC,
                            ScheduleShortBy.SCHEDULE_ID);
            assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedules_6() {
            // Setup
            final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                            .scheduleDTOs(Collections.emptyList())
                            .totalPages(null)
                            .total(null)
                            .sortOrder(null)
                            .sortBy(null)
                            .build();
            when(mockScheduleRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            // Run the test
            ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedules(ApiConstants.DEFAULT_PAGE,
                            null,
                            SortOrder.ASC,
                            ScheduleShortBy.SCHEDULE_ID);
            assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedulesByCronSchedule() {
        // Setup
        final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                .scheduleDTOs(List.of(ScheduleDTO.builder()
                        .id(0L)
                        .scheduleId("scheduleId")
                        .cronSchedule("cronSchedule")
                        .createdBy(0L)
                        .createdAt(Testconstants.DEFAULT_DATETIME)
                        .lastUpdatedBy(0L)
                        .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                        .build()))
                .totalPages(1)
                .total(1L)
                .sortOrder(SortOrder.ASC)
                .sortBy(ScheduleShortBy.SCHEDULE_ID)
                .build();

        // Configure ScheduleRepo.findByCronSchedule(...).
        final Page<Schedule> schedules = new PageImpl<>(List.of(Schedule.builder()
                .id(0L)
                .scheduleId("scheduleId")
                .cronSchedule("cronSchedule")
                .createdBy(0L)
                .createdAt(Testconstants.DEFAULT_DATETIME)
                .lastUpdatedBy(0L)
                .lastUpdatedAt(Testconstants.DEFAULT_DATETIME)
                .build()));
        when(mockScheduleRepo.findByCronSchedule(any(Pageable.class), eq("cronSchedule"))).thenReturn(schedules);

        // Run the test
        final ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedulesByCronSchedule(ApiConstants.DEFAULT_PAGE, ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                ScheduleShortBy.SCHEDULE_ID, "cronSchedule");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedulesByCronSchedule_ScheduleRepoReturnsNoItems() {
        // Setup
        final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                .scheduleDTOs(Collections.emptyList())
                .totalPages(null)
                .total(null)
                .sortOrder(null)
                .sortBy(null)
                .build();
        when(mockScheduleRepo.findByCronSchedule(any(Pageable.class), eq("cronSchedule")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final ScheduleDTOList result = scheduleServiceUnderTest.getAllSchedulesByCronSchedule(null, null, SortOrder.ASC,
                ScheduleShortBy.SCHEDULE_ID, "cronSchedule");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedulesByCronSchedule_3() {
        // Setup
        when(mockScheduleRepo.findByCronSchedule(any(Pageable.class), eq("cronSchedule")))
                .thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedulesByCronSchedule(null, null, SortOrder.ASC,
                ScheduleShortBy.SCHEDULE_ID, "cronSchedule")).isInstanceOf(InternalServerException.class);
    }

    @Test
    void testGetAllSchedulesByCronSchedule_4() {
        // Setup
        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedulesByCronSchedule(0, 0, null,
                null, "cronSchedule")).isInstanceOf(BadRequestException.class);
    }

    @Test
    void testGetAllSchedulesByCronSchedule_5() {
        // Setup
        final ScheduleDTOList expectedResult = ScheduleDTOList.builder()
                            .scheduleDTOs(Collections.emptyList())
                            .totalPages(null)
                            .total(null)
                            .sortOrder(null)
                            .sortBy(null)
                            .build();
                            when(mockScheduleRepo.findByCronSchedule(any(Pageable.class), eq("cronSchedule")))
                            .thenReturn(new PageImpl<>(Collections.emptyList()));
        // Run the test
        ScheduleDTOList result=scheduleServiceUnderTest.getAllSchedulesByCronSchedule(1, null, SortOrder.ASC,
        ScheduleShortBy.SCHEDULE_ID, "cronSchedule");
        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllSchedulesByCronSchedule_6() {
        // Setup
        when(mockScheduleRepo.findByCronSchedule(any(Pageable.class), eq("cronSchedule")))
                        .thenReturn(new PageImpl<>(Collections.emptyList()));
        // Run the test
        assertThatThrownBy(() -> scheduleServiceUnderTest.getAllSchedulesByCronSchedule(2,
                        ApiConstants.DEFAULT_PAGE_SIZE, SortOrder.ASC,
                        ScheduleShortBy.SCHEDULE_ID, "cronSchedule")).isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindOrCreateSchedule_ExistingScheduleFound() {
        // Setup
        ScheduleRequest scheduleRequest = ScheduleRequest.builder().scheduleId("existingScheduleId").cronSchedule("0/10 * * ? * *").build();
        Schedule existingSchedule = new Schedule();
        when(mockScheduleRepo.findByScheduleId("existingScheduleId")).thenReturn(Optional.of(existingSchedule));

        // Run the test
        Schedule result = scheduleServiceUnderTest.findOrCreateSchedule(scheduleRequest);

        // Verify the results
        assertThat(result).isEqualTo(existingSchedule);
        verify(mockScheduleRepo, times(1)).findByScheduleId("existingScheduleId");
        // verify(mockScheduleRepo, times(1)).save(any(Schedule.class));
        verifyNoMoreInteractions(mockScheduleRepo);
    }

    @Test
    void testFindOrCreateSchedule_NewScheduleCreated() {
        // Setup
        ScheduleRequest scheduleRequest = ScheduleRequest.builder().scheduleId("newScheduleId").cronSchedule("0/10 * * ? * *").build();
        Schedule newSchedule = new Schedule();
        when(mockScheduleRepo.findByScheduleId("newScheduleId")).thenReturn(Optional.empty());
        when(mockScheduleRepo.save(any(Schedule.class))).thenReturn(newSchedule);

        // Run the test
        Schedule result = scheduleServiceUnderTest.findOrCreateSchedule(scheduleRequest);

        // Verify the results
        assertThat(result).isEqualTo(newSchedule);
        verify(mockScheduleRepo, times(1)).findByScheduleId("newScheduleId");
        verify(mockScheduleRepo, times(1)).save(any(Schedule.class));
        verifyNoMoreInteractions(mockScheduleRepo);
    }
}
