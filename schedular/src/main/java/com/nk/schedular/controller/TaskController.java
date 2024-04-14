package com.nk.schedular.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coreoz.wisp.JobStatus;
import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.TaskDTO;
import com.nk.schedular.dto.TaskList;
import com.nk.schedular.dto.TaskRequest;
import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.enums.TaskShortBy;
import com.nk.schedular.service.SchedularService;
import com.nk.schedular.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private static final String DELETED_SUCCESSFULLY = "Deleted Successfully !!";
    private final SchedularService schedularService;
    private final TaskService taskService;

    
    /**
     * Start a Task with given taskId
     *
     * @param  taskId  unique taskId of the task
     * @return         WebResponse containing the JobStatus of the scheduled task
     */
    @Operation(summary = "Start a Task with given taskId")
    @Parameter(name = ApiConstants.TASK_ID, description = "unique taskId of the task", in = ParameterIn.QUERY)
    @GetMapping("/start")
    public ResponseEntity<WebResponse<JobStatus>> getMethodName(@RequestParam(value = ApiConstants.TASK_ID, required = true) String taskId) {
        JobStatus jobStatus = this.schedularService.scheduleTask(taskId);
        WebResponse<JobStatus> response = new WebResponse<>();
        response.setData(jobStatus);
        response.setMessage("Task Started Successfully !!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Cancel a Task with given taskId
     *
     * @param  taskId  unique taskId of the task
     * @return          WebResponse with a Boolean indicating if the task was cancelled successfully
     */
    @Operation(summary = "Cancel a Task with given taskId")
    @Parameter(name = ApiConstants.TASK_ID, description = "unique taskId of the task", in = ParameterIn.QUERY)
    @GetMapping("/cancel")
    public ResponseEntity<WebResponse<Boolean>> getCancelTask(@RequestParam(value = ApiConstants.TASK_ID, required = true) String taskId) {
        Boolean isCancelled = this.schedularService.cancelTask(taskId);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(isCancelled);
        response.setMessage("Task Canaled Successfully !!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Api to retrieve all the tasks.
     *
     * @param page     - page number
     * @param pageSize - number of elements in a page.
     * @return response object with the list of all Task and page
     *         information or error message if any.
     */
    @Operation(summary = "Get All Task")
    @Parameter(name = ApiConstants.PAGE, description = "page number", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE_SIZE, description = "number of elements per page", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.SORT_ORDER, description = "Order to sort in", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_ORDER)
    @Parameter(name = ApiConstants.SORT_BY, description = "value to sort by", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_CREATED)
    @GetMapping("/all")
    public ResponseEntity<WebResponse<TaskList>> getAllTask(
            @RequestParam(value = ApiConstants.PAGE, required = false) Integer page,
            @RequestParam(value = ApiConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = ApiConstants.SORT_ORDER, defaultValue = ApiConstants.DEFAULT_SORT_ORDER) SortOrder sort,
            @RequestParam(value = ApiConstants.SORT_BY, defaultValue = "id" ) TaskShortBy sortBy) {
        TaskList tasks = taskService.getAllTask(page, pageSize, sort, sortBy);
        WebResponse<TaskList> response = new WebResponse<>();
        response.setData(tasks);
        response.setMessage("Successfully fetched all tasks");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Saves a task by creating a TaskDTO from the provided TaskRequest.
     *
     * @param  taskRequest    the TaskRequest object containing task details
     * @return                a ResponseEntity containing a WebResponse with the saved TaskDTO and a success message
     */

    @Operation(summary = "Save a Task")
    @PostMapping
    public ResponseEntity<WebResponse<TaskDTO>> saveTask(@RequestBody TaskRequest taskRequest) {
        TaskDTO taskDTO = taskService.saveTask(taskRequest);
        WebResponse<TaskDTO> response = new WebResponse<>();
        response.setData(taskDTO);
        response.setMessage("Successfully saved task");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Save a Task
     *
     * @param  taskRequest   description of parameter
     * @return               description of return value
     */
    @Operation(summary = "Update a Task")
    @PutMapping
    public ResponseEntity<WebResponse<TaskDTO>> updateTask(@RequestBody TaskRequest taskRequest) {
        TaskDTO taskDTO = taskService.updateTask(taskRequest);
        WebResponse<TaskDTO> response = new WebResponse<>();
        response.setData(taskDTO);
        response.setMessage("Successfully updated task");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a Task with given taskId.
     *
     * @param  taskId	Task id to be deleted
     * @return         	WebResponse with a boolean indicating deletion success and a message
     */
    @Operation(summary = "Delete a Task with given taskId")
    @Parameter(name = ApiConstants.TASK_ID, description = "Task taskId to be deleted", in = ParameterIn.QUERY)
    @DeleteMapping
    public ResponseEntity<WebResponse<Boolean>> deleteTaskWithTaskID(@RequestParam(value = ApiConstants.TASK_ID, required = true) String taskId) {
        taskService.deleteTask(taskId);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(true);
        response.setMessage(DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a Task with given taskId
     *
     * @param  id	Task id to be deleted
     * @return     WebResponse<Boolean> object with deletion status
     */
    @Operation(summary = "Delete a Task with given taskId")
    @Parameter(name = ApiConstants.ID, description = "Task id to be deleted", in = ParameterIn.QUERY)
    @DeleteMapping(path = "/with-id")
    public ResponseEntity<WebResponse<Boolean>> deleteTaskWithID(@RequestParam(value = ApiConstants.TASK_ID, required = true) Long id) {
        taskService.deleteTask(id);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(true);
        response.setMessage(DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes tasks with the provided IDs.
     *
     * @param  ids   list of IDs of tasks to be deleted
     * @return       a response entity indicating the success of the deletion operation
     */
    @Operation(summary = "Delete a Task with given ids")
    @DeleteMapping(path = "/with-ids")
    public ResponseEntity<WebResponse<Boolean>> deleteTaskWitsIDs(@RequestBody List<Long> ids) {
        taskService.deleteTasksWithIds(ids);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(true);
        response.setMessage(DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a Task with given taskIDs
     *
     * @param  taskIDs   List of taskIDs to delete
     * @return          ResponseEntity containing a WebResponse with a Boolean indicating success and a message
     */
    @Operation(summary = "Delete a Task with given taskIDs")
    @DeleteMapping(path = "/with-task-ids")
    public ResponseEntity<WebResponse<Boolean>> deleteTaskWithTaskIDs(@RequestBody List<String> taskIDs) {
        taskService.deleteTasks(taskIDs);
        WebResponse<Boolean> response = new WebResponse<>();
        response.setData(true);
        response.setMessage(DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

     
    @Operation(summary = "Get All Task with schedule ID")
    @Parameter(name = ApiConstants.SCHEDULE_ID, description = "schedule id", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE, description = "page number", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE_SIZE, description = "number of elements per page", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.SORT_ORDER, description = "Order to sort in", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_ORDER)
    @Parameter(name = ApiConstants.SORT_BY, description = "value to sort by", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_CREATED)
    @GetMapping(path = "/with-schedule-id")
    public ResponseEntity<WebResponse<TaskList>> getAllTaskWithScheduleID(
            @RequestParam(value = ApiConstants.PAGE, required = false) Integer page,
            @RequestParam(value = ApiConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = ApiConstants.SORT_ORDER, defaultValue = ApiConstants.DEFAULT_SORT_ORDER) SortOrder sort,
            @RequestParam(value = ApiConstants.SORT_BY, defaultValue = "id" ) TaskShortBy sortBy,
            @RequestParam(value = ApiConstants.SCHEDULE_ID, required = true) String scheduleId) {
        TaskList configurations = taskService.getAllTaskBySchedule(page, pageSize, sort, sortBy, scheduleId);
        WebResponse<TaskList> response = new WebResponse<>();
        response.setData(configurations);
        response.setMessage("Successfully fetched all task for given schedule");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get All Task with cron expression
     *
     * @param  page           page number
     * @param  pageSize       number of elements per page
     * @param  sort           Order to sort in
     * @param  sortBy         value to sort by
     * @param  cronExpression cron expression
     * @return               ResponseEntity<WebResponse<TaskList>>
     */
    @Operation(summary = "Get All Task with cron expression")
    @Parameter(name = ApiConstants.CRON_EXPRESSION, description = "cron expression", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE, description = "page number", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.PAGE_SIZE, description = "number of elements per page", in = ParameterIn.QUERY)
    @Parameter(name = ApiConstants.SORT_ORDER, description = "Order to sort in", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_ORDER)
    @Parameter(name = ApiConstants.SORT_BY, description = "value to sort by", in = ParameterIn.QUERY, example = ApiConstants.DEFAULT_SORT_CREATED)
    @GetMapping(path = "/with-cron-expression")
    public ResponseEntity<WebResponse<TaskList>> getAllTaskWithCronExpression(
            @RequestParam(value = ApiConstants.PAGE, required = false) Integer page,
            @RequestParam(value = ApiConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = ApiConstants.SORT_ORDER, defaultValue = ApiConstants.DEFAULT_SORT_ORDER) SortOrder sort,
            @RequestParam(value = ApiConstants.SORT_BY, defaultValue = "id" ) TaskShortBy sortBy,
            @RequestParam(value = ApiConstants.CRON_EXPRESSION, required = true) String cronExpression) {
        TaskList taskList = taskService.getAllTaskByCronExp(page, pageSize, sort, sortBy, cronExpression);
        WebResponse<TaskList> response = new WebResponse<>();
        response.setData(taskList);
        response.setMessage("Successfully fetched all task for given cron expression");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
