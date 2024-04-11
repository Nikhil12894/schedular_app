package com.nk.schedular.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.nk.schedular.Util.Util;
import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.ScheduleDTO;
import com.nk.schedular.dto.ScheduleRequest;
import com.nk.schedular.dto.SortOrder;
import com.nk.schedular.dto.TaskDTO;
import com.nk.schedular.dto.TaskList;
import com.nk.schedular.dto.TaskRequest;
import com.nk.schedular.dto.TaskShortBy;
import com.nk.schedular.dto.UpdateTaskRequest;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.model.DemoTask;
import com.nk.schedular.model.Schedule;
import com.nk.schedular.repo.TaskRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService {

    private static final String INVALID_PAGE="Invalid page number, number of available pages is ";
    private static final String TASK_ERROR="Exception occurred while getting tasks.";
    private final TaskRepo taskRepo;

  
    /**
     * Save a task using the given task request.
     *
     * @param  task  the task request to save
     * @return       the TaskDTO representing the saved task
     */
    public TaskDTO saveTask(TaskRequest task) {
        ScheduleRequest scheduleRequest = task.getSchedule();
        DemoTask demoTaskToSave = DemoTask.builder()
        .taskId(task.getTaskId())
        .description(task.getDescription())
        .isSchedularEnabled(task.getIsSchedularEnabled())
        .schedule(
            Schedule.builder()
            .id(scheduleRequest.getId())
            .scheduleId(scheduleRequest.getScheduleId())
            .cronSchedule(scheduleRequest.getCronSchedule())
            .build()
            )
        .build();
        this.setAuditFields(demoTaskToSave);
        DemoTask savedTask = this.taskRepo.save(demoTaskToSave);
        return mapDemoTaskToTaskDTO(savedTask);
    }

    /**
     * Sets audit fields for the given DemoTask if they are null or 0.
     *
     * @param  demoTaskToSave    the DemoTask object to update audit fields
     */
    private void setAuditFields(DemoTask demoTaskToSave) {
        if(demoTaskToSave.getId() == null || demoTaskToSave.getId() == 0) {
            demoTaskToSave.setCreatedBy(0L);
            demoTaskToSave.setCreatedAt(Util.getCurrentTimestamp());
        }
        demoTaskToSave.setLastUpdatedBy(0L);
        demoTaskToSave.setLastUpdatedAt(Util.getCurrentTimestamp());
    }

    /**
     * Retrieves a TaskDTO by taskId.
     *
     * @param  taskId  the unique identifier of the task
     * @return          the TaskDTO object corresponding to the taskId
     */
    public TaskDTO getTask(String taskId) {
        DemoTask task = this.taskRepo.findByTaskId(taskId)
                .orElseThrow(() -> new BadRequestException(ApiConstants.INVALID_TASK_ID));
        return mapDemoTaskToTaskDTO(task);
    }

    /**
     * Update a task with the given task details.
     *
     * @param  task   the UpdateTaskRequest containing the details to update the task
     * @return       the TaskDTO representing the updated task
     */
    public TaskDTO updateTask(UpdateTaskRequest task) {
        ScheduleRequest scheduleRequest = task.getSchedule();
        DemoTask demoTaskToUpdate = DemoTask.builder()
        .id(task.getId())
        .taskId(task.getTaskId())
        .description(task.getDescription())
        .isSchedularEnabled(task.getIsSchedularEnabled())
        .schedule(
            Schedule.builder()
            .id(scheduleRequest.getId())
            .scheduleId(scheduleRequest.getScheduleId())
            .cronSchedule(scheduleRequest.getCronSchedule())
            .build()
            )
        .build();
        this.setAuditFields(demoTaskToUpdate);
        DemoTask updatedTask = this.taskRepo.save(demoTaskToUpdate);
        return mapDemoTaskToTaskDTO(updatedTask);
    }

    /**
     * Deletes a task with the given ID.
     *
     * @param  id  the ID of the task to be deleted
     * @return     void
     */
    public void deleteTask(Long id) {
        log.debug("Deleting task with ID: {}", id); // Log the task ID being deleted
        this.taskRepo.deleteById(id); // Delete the task
        log.debug("Deleted task with ID: {}", id); // Log the successful deletion of the task
    }

    /**
     * Deletes a task based on the provided task ID.
     *
     * @param  taskId  the ID of the task to be deleted
     * @return         void
     */
    public void deleteTask(String taskId) {
        log.debug("Deleting task with ID: {}", taskId); // Log the task ID being deleted
        this.taskRepo.deleteByTaskId(taskId); // Delete the task
        log.debug("Deleted task with ID: {}", taskId); // Log the successful deletion of the task
    }


    /**
     * Deletes tasks based on the provided task IDs.
     *
     * @param  taskIds    a list of task IDs to be deleted
     */
    public void deleteTasks(List<String> taskIds) {
        log.debug("Deleting tasks with IDs: {}", taskIds); // Log the task IDs being deleted
        this.taskRepo.deleteByTaskIds(taskIds); // Delete tasks with the provided IDs
        log.debug("Deleted tasks with IDs: {}", taskIds); // Log the successful deletion of tasks
    }

    /**
     * Deletes multiple tasks identified by their ids.
     *
     * @param ids List of ids of tasks to be deleted.
     */
    public void deleteTasksWithIds(List<Long> ids) {
        // Log the task ids being deleted
        log.info("Deleting tasks with ids: " + ids);
        // Deletes multiple tasks identified by their ids.
        this.taskRepo.deleteByIds(ids);
        // Log the task deletion success
        log.info("Deleted tasks with ids: " + ids);
    }

    /**
     * Get a list of tasks by task IDs with pagination and sorting options.
     *
     * @param  taskIds   a list of task IDs to filter the tasks
     * @param  page      the page number for pagination
     * @param  pageSize  the size of each page for pagination
     * @param  sort      the sorting order
     * @param  sortBy    the field to sort by
     * @return          a TaskList object containing the filtered and sorted tasks
     */
    public TaskList getTaskByTaskIds(List<String> taskIds,Integer page, Integer pageSize, SortOrder sort, TaskShortBy sortBy) {

        TaskList.TaskListBuilder listBuilder = TaskList.builder().tasks(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<DemoTask> pagedTasks = taskRepo.findAllTaskWithTaskIds(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null),taskIds);
            if (page > pagedTasks.getTotalPages()) {
                throw new BadRequestException(
                    INVALID_PAGE + pagedTasks.getTotalPages());
            }
            if (pagedTasks.hasContent()) {
                List<TaskDTO> taskDTOList = pagedTasks.getContent().stream()
                .map(this::mapDemoTaskToTaskDTO).collect(Collectors.toList());
                listBuilder.tasks(taskDTOList)
                        .total(pagedTasks.getTotalElements())
                        .totalPages(pagedTasks.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting tasks. {}", e);
            throw new InternalServerException(TASK_ERROR);
        }
        return listBuilder.build();
    }

    /**
     * A description of the entire Java function.
     *
     * @param page     description of parameter
     * @param pageSize description of parameter
     * @param sort     description of parameter
     * @param sortBy   description of parameter
     * @return description of return value
     */
    public TaskList getAllTask(Integer page, Integer pageSize, SortOrder sort, TaskShortBy sortBy) {
        TaskList.TaskListBuilder listBuilder = TaskList.builder().tasks(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<DemoTask> pagedTasks = taskRepo.findAll(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null));
            if (page > pagedTasks.getTotalPages()) {
                throw new BadRequestException(
                    INVALID_PAGE + pagedTasks.getTotalPages());
            }
            if (pagedTasks.hasContent()) {
                List<TaskDTO> taskDTOList = pagedTasks.getContent().stream()
                .map(this::mapDemoTaskToTaskDTO).collect(Collectors.toList());
                listBuilder.tasks(taskDTOList)
                        .total(pagedTasks.getTotalElements())
                        .totalPages(pagedTasks.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting tasks. {}", e);
            throw new InternalServerException(TASK_ERROR);
        }
        return listBuilder.build();
    }

    /**
     * Retrieves all tasks for a given schedule, with optional pagination and
     * sorting.
     *
     * @param page       the page number for pagination
     * @param pageSize   the size of each page for pagination
     * @param sort       the sorting order for the tasks
     * @param sortBy     the attribute to sort the tasks by
     * @param scheduleId the ID of the schedule for which tasks are retrieved
     * @return the list of tasks based on the specified criteria
     */
    public TaskList getAllTaskBySchedule(Integer page, Integer pageSize, SortOrder sort, TaskShortBy sortBy,
            String scheduleId) {
        TaskList.TaskListBuilder listBuilder = TaskList.builder().tasks(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<DemoTask> pagedTasks = taskRepo.findAll(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null),
                    scheduleId);
            if (page > pagedTasks.getTotalPages()) {
                throw new BadRequestException(
                    INVALID_PAGE + pagedTasks.getTotalPages());
            }
            if (pagedTasks.hasContent()) {
                List<TaskDTO> taskDTOList = pagedTasks.getContent().stream()
                .map(this::mapDemoTaskToTaskDTO).collect(Collectors.toList());
                listBuilder.tasks(taskDTOList)
                        .total(pagedTasks.getTotalElements())
                        .totalPages(pagedTasks.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting tasks. {} ", e);
            throw new InternalServerException(TASK_ERROR);
        }
        return listBuilder.build();
    }

    /**
     * A description of the entire Java function.
     *
     * @param  page         description of parameter
     * @param  pageSize     description of parameter
     * @param  sort         description of parameter
     * @param  sortBy       description of parameter
     * @param  cronSchedule description of parameter
     * @return              description of return value
     */
    public TaskList getAllTaskByCronExp(Integer page, Integer pageSize, SortOrder sort, TaskShortBy sortBy,
            String cronSchedule) {
        TaskList.TaskListBuilder listBuilder = TaskList.builder().tasks(new ArrayList<>());
        try {
            // if no page or pagesize specified return all tasks
            if (page == null || pageSize == null) {
                page = ApiConstants.DEFAULT_PAGE;
                pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
            }
            Page<DemoTask> pagedTasks = taskRepo.findAllWithCron(Util.getPageable(page,
                    pageSize, null != sort ? sort.name() : null, null != sortBy ? sortBy.getOrderBy() : null),
                    cronSchedule);
            if (page > pagedTasks.getTotalPages()) {
                throw new BadRequestException(
                    INVALID_PAGE + pagedTasks.getTotalPages());
            }
            if (pagedTasks.hasContent()) {
                List<TaskDTO> taskDTOList = pagedTasks.getContent().stream()
                .map(this::mapDemoTaskToTaskDTO).collect(Collectors.toList());
                listBuilder.tasks(taskDTOList)
                        .total(pagedTasks.getTotalElements())
                        .totalPages(pagedTasks.getTotalPages())
                        .sortBy(sortBy).sortOrder(sort);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while getting tasks. {} ", e);
            throw new InternalServerException(TASK_ERROR);
        }
        return listBuilder.build();
    }


    /**
     * Maps a DemoTask object to a TaskDTO object.
     *
     * @param  DemoTask   the DemoTask object to be mapped
     * @return           the mapped TaskDTO object
     */
    private TaskDTO mapDemoTaskToTaskDTO(DemoTask DemoTask) {
        return TaskDTO.builder()
                .id(DemoTask.getId())
                .description(DemoTask.getDescription())
                .isSchedularEnabled(DemoTask.getIsSchedularEnabled())
                .schedule(mapScheduleToDTO(DemoTask.getSchedule()))
                .createdAt(DemoTask.getCreatedAt())
                .createdBy(DemoTask.getCreatedBy())
                .lastUpdatedAt(DemoTask.getLastUpdatedAt())
                .lastUpdatedBy(DemoTask.getLastUpdatedBy())
                .build();
    }

    /**
     * Maps a Schedule object to a ScheduleDTO object.
     *
     * @param  schedule    the Schedule object to be mapped
     * @return             the ScheduleDTO object mapped from the input Schedule
     */
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
