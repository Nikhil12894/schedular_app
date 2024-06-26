package com.nk.schedular.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleDTOList;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.enums.ScheduleShortBy;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Save a new schedule.
     *
     * @param schedule the Schedule object to be saved
     * @return a response entity with the saved ScheduleDTO
     */
    @Operation(summary = "Save schedule")
    @PostMapping
    public ResponseEntity<WebResponse<ScheduleDTO>> saveSchedule(@RequestBody ScheduleRequest schedule) {
        ScheduleDTO savedSchedule = scheduleService.saveSchedule(schedule);
        WebResponse<ScheduleDTO> response = new WebResponse<>();
        response.setData(savedSchedule);
        response.setMessage("Saved Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Update a schedule.
     *
     * @param schedule description of the schedule to be updated
     * @return a response entity with the updated schedule data
     */
    @Operation(summary = "Update schedule")
    @PutMapping
    public ResponseEntity<WebResponse<ScheduleDTO>> updateSchedule(@RequestBody ScheduleRequest schedule) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(schedule);
        WebResponse<ScheduleDTO> response = new WebResponse<>();
        response.setData(updatedSchedule);
        response.setMessage("Updated Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * A description of the entire Java function.
     *
     * @param scheduleId description of parameter
     * @return description of return value
     */
    @Operation(summary = "Get Schedule with scheduleId")
    @GetMapping
    public ResponseEntity<WebResponse<ScheduleDTO>> getSchedule(
            @RequestParam(value = ApiConstants.SCHEDULE_ID, required = true) String scheduleId) {
        ScheduleDTO scheduleDTO = scheduleService.getSchedule(scheduleId);
        WebResponse<ScheduleDTO> response = new WebResponse<>();
        response.setData(scheduleDTO);
        response.setMessage("Fetched Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Get All Task
     *
     * @param page     page number
     * @param pageSize number of elements per page
     * @param sort     Order to sort in
     * @param sortBy   value to sort by
     * @return description of return value
     */
    @Operation(summary = "Get All Schedule")
    @Parameter(name = ApiConstants.PAGE, description = "page number", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE_SIZE, description = "number of elements per page", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.SORT_ORDER, description = "Order to sort in", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_ORDER)
    @Parameter(name = ApiConstants.SORT_BY, description = "value to sort by", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Schedules"),
            @ApiResponse(responseCode = "400", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<WebResponse<ScheduleDTOList>> getAllSchedule(
            @RequestParam(value = ApiConstants.PAGE, required = false) Integer page,
            @RequestParam(value = ApiConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = ApiConstants.SORT_ORDER, defaultValue = ApiConstants.DEFAULT_SORT_ORDER) SortOrder sort,
            @RequestParam(value = ApiConstants.SORT_BY, defaultValue = "id") ScheduleShortBy sortBy) {
        ScheduleDTOList scheduleDTOList = scheduleService.getAllSchedules(page, pageSize, sort, sortBy);
        WebResponse<ScheduleDTOList> response = new WebResponse<>();
        response.setData(scheduleDTOList);
        response.setMessage("Fetched Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Get All Schedule with cron expression
     *
     * @param cronExpression cron expression
     * @param page           page number
     * @param pageSize       number of elements per page
     * @param sort           Order to sort in
     * @param sortBy         value to sort by
     * @return ResponseEntity<WebResponse<ScheduleDTOList>>
     */
    @Operation(summary = "Get All Schedule with cron expression")
    @Parameter(name = ApiConstants.CRON_EXPRESSION, description = "cron expression", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE, description = "page number", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE_SIZE, description = "number of elements per page", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.SORT_ORDER, description = "Order to sort in", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_ORDER)
    @Parameter(name = ApiConstants.SORT_BY, description = "value to sort by", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_CREATED)
    @GetMapping(path = "/with-cron-expression")
    public ResponseEntity<WebResponse<ScheduleDTOList>> getAllSchedulesWithCronExpration(
            @RequestParam(value = ApiConstants.CRON_EXPRESSION, required = true) String cronExpression,
            @RequestParam(value = ApiConstants.PAGE, required = false) Integer page,
            @RequestParam(value = ApiConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = ApiConstants.SORT_ORDER, defaultValue = ApiConstants.DEFAULT_SORT_ORDER) SortOrder sort,
            @RequestParam(value = ApiConstants.SORT_BY, defaultValue = "id") ScheduleShortBy sortBy) {
        ScheduleDTOList scheduleDTOList = scheduleService.getAllSchedulesByCronSchedule(page, pageSize, sort, sortBy,
                cronExpression);
        WebResponse<ScheduleDTOList> response = new WebResponse<>();
        response.setData(scheduleDTOList);
        response.setMessage("Fetched Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a schedule based on the provided scheduleId.
     *
     * @param scheduleId The ID of the schedule to be deleted
     * @return A WebResponse indicating if the schedule was successfully deleted
     */

    @Operation(summary = "Delete a schedule based on the provided scheduleId")
    @Parameter(name = ApiConstants.SCHEDULE_ID, description = "Schedule Id", in = ParameterIn.QUERY)
    @DeleteMapping
    public ResponseEntity<WebResponse<Boolean>> deleteSchedule(
            @RequestParam(value = ApiConstants.SCHEDULE_ID, required = true) String scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(true);
        response.setMessage("Deleted Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all distinct cron expressions.
     *
     * @return A ResponseEntity containing a WebResponse with a list of distinct
     *         cron expressions and a success message.
     */
    @Operation(summary = "Get all distinct cron expression")
    @GetMapping(path = "/distinct-cron-expression")
    public ResponseEntity<WebResponse<List<String>>> distinctCronExp() {
        List<String> distinctCronExpression = scheduleService.getDistinctCronExpression();
        WebResponse<List<String>> response = new WebResponse<>();
        response.setData(distinctCronExpression);
        response.setMessage(" Successfully !!");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all distinct cron expressions and returns them as a response
     * entity.
     *
     * @return A ResponseEntity containing a WebResponse with a list of distinct
     *         cron expressions and a success message.
     */
    @Operation(summary = "Get all schedule ID")
    @GetMapping(path = "/all-schedule-id")
    public ResponseEntity<WebResponse<List<String>>> allScheduleID() {
        List<String> distinctCronExpression = scheduleService.getAllScheduleID();
        WebResponse<List<String>> response = new WebResponse<>();
        response.setData(distinctCronExpression);
        response.setMessage(" Successfully !!");
        return ResponseEntity.ok(response);
    }

}
