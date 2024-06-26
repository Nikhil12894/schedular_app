package com.nk.schedular.service;

import org.springframework.stereotype.Service;

import com.coreoz.wisp.Job;
import com.coreoz.wisp.JobStatus;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.cron.CronExpressionSchedule;
import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.model.DemoTask;
import com.nk.schedular.repo.TaskRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedularService {

    private final Scheduler scheduler;
    private final TaskRepo taskRepo;



    /**
     * A description of the entire Java function.
     *
     * @param  taskId	description of parameter
     * @return        	job status
     */
    public JobStatus scheduleTask(String taskId){
        DemoTask task = this.taskRepo.findByTaskId(taskId).orElseThrow(() -> new BadRequestException(ApiConstants.INVALID_TASK_ID));
        Job job = this.scheduler.schedule(task.getTaskId(), task, CronExpressionSchedule.parseWithSeconds(task.getSchedule().getCronSchedule()));
        log.info("Task Scheduled Successfully !! {}", task.getTaskId());
        return job.status();
    }
    
    /**
     * Cancels a task with the given task ID.
     *
     * @param  taskId  the ID of the task to be cancelled
     * @return         true if the task was successfully cancelled
     */
    public boolean cancelTask(String taskId){
        try {
            this.scheduler.cancel(taskId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Task cancellation timed out: " + taskId);
        }
        return true;
    }

}
