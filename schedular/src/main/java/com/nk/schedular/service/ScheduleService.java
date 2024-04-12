package com.nk.schedular.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.nk.schedular.Util.Util;
import com.nk.schedular.Util.Util.PagePageSizeRecord;
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
        checkIfScheduleAlreadyExist(schedule.getScheduleId());
        this.setAuditFields(scheduleToSave);
        scheduleRepo.save(scheduleToSave);
        log.info("Saved schedule with id: " + scheduleToSave.getScheduleId());
        return this.mapScheduleToDTO(scheduleToSave);
    }

    /**
     * Builds a Schedule object based on the provided ScheduleRequest.
     *
     * @param  schedule  the ScheduleRequest to build the Schedule from
     * @return           the built Schedule object
     */
    private Schedule buildSchedule(ScheduleRequest schedule) {
        if (!CronExpression.isValidExpression(schedule.getCronSchedule())) {
            throw new BadRequestException("Bad cron Syntax");
        }
        return new Schedule(schedule.getId(), schedule.getScheduleId(), schedule.getCronSchedule());
    }

    /**
     * Checks if a schedule already exists by the provided scheduleId.
     *
     * @param  scheduleId  the id of the schedule to check for existence
     */
    private void checkIfScheduleAlreadyExist(String scheduleId) {
        if (scheduleRepo.findByScheduleId(scheduleId).isPresent()) {
            log.error("Schedule with id: {} already exists", scheduleId);
            throw new DuplicateTransactionException(ApiConstants.SCHEDULE_ALREADY_EXISTS);
        }
    }

    /**
     * Checks if a schedule with the given id exists. If it does not,
     * throws a NotFoundException.
     *
     * @param id The id of the schedule to check
     * @throws NotFoundException if the schedule with the given id does not exist
     */
    private void checkIfScheduleExists(Long id) {
        if (scheduleRepo.findById(id).isEmpty()) {
            throw new NotFoundException(ApiConstants.SCHEDULE_NOT_FOUND);
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
        this.checkIfScheduleExists(schedule.getId());
        Schedule scheduleToSave = this.buildSchedule(schedule);
        this.setAuditFields(scheduleToSave);
        scheduleRepo.save(scheduleToSave);
        log.info("Updated schedule with id: " + scheduleToSave.getScheduleId());
        return this.mapScheduleToDTO(scheduleToSave);
    }

    /**
     * Deletes a schedule based on the provided scheduleId.
     *
     * @param  scheduleId  the schedule id to delete
     * @return         	void
     */
    public void deleteSchedule(String scheduleId) {
        scheduleRepo.deleteByScheduleId(scheduleId);
        log.info("Deleted schedule with id: " + scheduleId);
    }

    /**
     * Get Schedule with scheduleId
     *
     * @param  scheduleId  description of parameter
     * @return            description of return value
     */
    public ScheduleDTO getSchedule(String scheduleId) {
        if (scheduleId == null || scheduleId.isEmpty()) {
            throw new BadRequestException("Invalid schedule ID");
        }
        Schedule schedule = scheduleRepo.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(ApiConstants.INVALID_SCHEDULE_ID));
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
            PagePageSizeRecord validatedPagePageSize = Util.getResult(page, pageSize);
            Page<Schedule> pagedSchedules = scheduleRepo.findAll(Util.getPageable(validatedPagePageSize.page(),
            validatedPagePageSize.pageSize(), null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null));
            this.validateAndAddDataToListBuilder(sort, sortBy, listBuilder, validatedPagePageSize, pagedSchedules);
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
            PagePageSizeRecord validatedPagePageSize = Util.getResult(page, pageSize);
            Page<Schedule> pagedSchedules = scheduleRepo.findByCronSchedule(Util.getPageable(validatedPagePageSize.page(),
            validatedPagePageSize.pageSize(), null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null),
                    cronSchedule);
            this.validateAndAddDataToListBuilder(sort, sortBy, listBuilder, validatedPagePageSize, pagedSchedules);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting schedules. {}", e);
            throw new InternalServerException("Exception occurred while getting schedules.");
        }
        return listBuilder.build();
    }

    
    /**
     * Validates and adds data to the ScheduleDTOListBuilder based on sorting criteria.
     *
     * @param  sort                the sort order to be applied
     * @param  sortBy              the criteria to sort schedules by
     * @param  listBuilder         the ScheduleDTOListBuilder to add schedules to
     * @param  validatedPagePageSize  the page and page size record
     * @param  pagedSchedules the paged schedules to process
     */
    private void validateAndAddDataToListBuilder(SortOrder sort, ScheduleShortBy sortBy, ScheduleDTOList.ScheduleDTOListBuilder listBuilder,
            PagePageSizeRecord validatedPagePageSize, Page<Schedule> pagedSchedules) {
        if (validatedPagePageSize.page() > pagedSchedules.getTotalPages()) {
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
    }

    /**
     * Maps a Schedule object to a ScheduleDTO object.
     *
     * @param  schedule   the Schedule object to be mapped
     * @return           the mapped ScheduleDTO object
     */
    public ScheduleDTO mapScheduleToDTO(Schedule schedule) {
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

    /**
     * Finds or creates a schedule based on the provided schedule request.
     *
     * @param  scheduleRequest  the schedule request
     * @return                 the found or newly created schedule
     */
    public Schedule findOrCreateSchedule(ScheduleRequest scheduleRequest) {
        String scheduleId = scheduleRequest.getScheduleId();
        return scheduleRepo.findByScheduleId(scheduleId)
                .orElseGet(() -> {
                    Schedule scheduleToSave = this.buildSchedule(scheduleRequest);
                    this.setAuditFields(scheduleToSave);
                    return scheduleRepo.save(scheduleToSave);
                });
    }

}
