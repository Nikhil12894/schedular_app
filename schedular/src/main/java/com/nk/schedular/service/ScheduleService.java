package com.nk.schedular.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.nk.schedular.Util.Util;
import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleDTOList;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.ScheduleShortBy;
import com.nk.schedular.dto.SortOrder;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.DuplicateTransactionException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.exception.NotFoundException;
import com.nk.schedular.model.Schedule;
import com.nk.schedular.repo.ScheduleRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;

    /**
     * Save a schedule based on the provided schedule request.
     *
     * @param schedule the schedule request to save
     * @return the saved schedule DTO
     */
    public ScheduleDTO saveSchedule(ScheduleRequest schedule) {
        Schedule scheduleToSave = this.buildSchedule(schedule);
        this.setAuditFields(scheduleToSave);
        scheduleRepo.save(scheduleToSave);
        log.info("Saved schedule with id: " + scheduleToSave.getScheduleId());
        return this.mapScheduleToDTO(scheduleToSave);
    }

    private Schedule buildSchedule(ScheduleRequest schedule) {
        if (!CronExpression.isValidExpression(schedule.getCronSchedule())) {
            throw new BadRequestException("Bad cron Syntax");
        }
        this.checkIfScheduleAlreadyExist(schedule.getScheduleId());
        return Schedule.builder()
        .id(schedule.getId())
        .scheduleId(schedule.getScheduleId())
        .cronSchedule(schedule.getCronSchedule())
        .build();
    }

    private void checkIfScheduleAlreadyExist(String scheduleId) {
        if (Boolean.TRUE.equals(scheduleRepo.existsByScheduleId(scheduleId))) {
            log.error("Schedule with id: {} already exists", scheduleId);
            throw new DuplicateTransactionException(ApiConstants.SCHEDULE_ALREADY_EXISTS);
        }
    }

    /**
     * Sets the audit fields for the given Schedule object. If the Schedule's ID is
     * null or 0,
     * it sets the createdBy and createdAt fields. It always sets lastUpdatedBy and
     * lastUpdatedAt fields.
     *
     * @param schedule The Schedule object for which to set audit fields
     */
    private void setAuditFields(Schedule schedule) {
        LocalDateTime currentTimestamp = Util.getCurrentTimestamp();
        if (schedule.getId() == null || schedule.getId() == 0) {
            schedule.setCreatedBy(0L);
            schedule.setCreatedAt(currentTimestamp);
        }
        schedule.setLastUpdatedBy(0L);
        schedule.setLastUpdatedAt(currentTimestamp);
    }

    /**
     * Updates a schedule based on the provided ScheduleRequest and returns
     * the updated ScheduleDTO.
     *
     * @param schedule the ScheduleRequest containing the updated schedule
     *                 information
     * @return the updated ScheduleDTO
     */
    public ScheduleDTO updateSchedule(ScheduleRequest schedule) {
        if (Boolean.FALSE.equals(!scheduleRepo.existsByScheduleId(schedule.getScheduleId()))) {
            throw new NotFoundException("Schedule not found");
        }
        Schedule scheduleToSave = this.buildSchedule(schedule);
        this.setAuditFields(scheduleToSave);
        scheduleRepo.save(scheduleToSave);
        log.info("Updated schedule with id: " + scheduleToSave.getScheduleId());
        return this.mapScheduleToDTO(scheduleToSave);
    }

    public void deleteSchedule(String scheduleId) {
        scheduleRepo.deleteByScheduleId(scheduleId);
        log.info("Deleted schedule with id: " + scheduleId);
    }

    public ScheduleDTO getSchedule(String scheduleId) {
        if (scheduleId == null || scheduleId.isEmpty()) {
            throw new BadRequestException("Invalid schedule ID");
        }
        Schedule schedule = scheduleRepo.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(ApiConstants.INVALID_SCHEDULE_ID));
        log.info("Saved schedule with id: " + schedule.getScheduleId());
        return this.mapScheduleToDTO(schedule);
    }

    /**
     * Retrieves all schedules from the database and maps them to ScheduleDTO
     * objects.
     *
     * @return a list of ScheduleDTO objects representing all schedules
     */
    public ScheduleDTOList getAllSchedules(Integer page, Integer pageSize, SortOrder sort, ScheduleShortBy sortBy) {
        ScheduleDTOList.ScheduleDTOListBuilder listBuilder = ScheduleDTOList.builder().scheduleDTOs(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<Schedule> pagedSchedules = scheduleRepo.findAll(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null));
            if (page > pagedSchedules.getTotalPages()) {
                throw new BadRequestException(
                        "Invalid page number, number of available pages is " + pagedSchedules.getTotalPages());
            }
            if (pagedSchedules.hasContent()) {
                List<ScheduleDTO> scheduleDTOs = pagedSchedules.getContent().stream()
                        .map(this::mapScheduleToDTO).collect(Collectors.toList());
                listBuilder.scheduleDTOs(scheduleDTOs)
                        .total(pagedSchedules.getTotalElements())
                        .totalPages(pagedSchedules.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting schedules. {}", e);
            throw new InternalServerException("Exception occurred while getting schedules.");
        }
        return listBuilder.build();
    }

    /**
     * Retrieves all schedules based on the provided cron schedule.
     *
     * @param cronSchedule the cron schedule to filter schedules
     * @return a list of ScheduleDTO objects corresponding to the schedules found
     */
    public ScheduleDTOList getAllSchedulesByCronSchedule(Integer page, Integer pageSize,
            SortOrder sort, ScheduleShortBy sortBy, String cronSchedule) {
        ScheduleDTOList.ScheduleDTOListBuilder listBuilder = ScheduleDTOList.builder().scheduleDTOs(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<Schedule> pagedSchedules = scheduleRepo.findByCronSchedule(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null),
                    cronSchedule);
            if (page > pagedSchedules.getTotalPages()) {
                throw new BadRequestException(
                        "Invalid page number, number of available pages is " + pagedSchedules.getTotalPages());
            }
            if (pagedSchedules.hasContent()) {
                List<ScheduleDTO> scheduleDTOs = pagedSchedules.getContent().stream()
                        .map(this::mapScheduleToDTO).collect(Collectors.toList());
                listBuilder.scheduleDTOs(scheduleDTOs)
                        .total(pagedSchedules.getTotalElements())
                        .totalPages(pagedSchedules.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting schedules. {}", e);
            throw new InternalServerException("Exception occurred while getting schedules.");
        }
        return listBuilder.build();
    }

    private ScheduleDTO mapScheduleToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .scheduleId(schedule.getScheduleId())
                .cronSchedule(schedule.getCronSchedule())
                .lastUpdatedAt(schedule.getLastUpdatedAt())
                .lastUpdatedBy(schedule.getLastUpdatedBy())
                .createdAt(schedule.getCreatedAt())
                .createdBy(schedule.getCreatedBy())
                .build();
    }
}
