package com.nk.schedular.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.nk.schedular.model.DemoTask;

public interface TaskRepo extends ListCrudRepository<DemoTask, Long>, ListPagingAndSortingRepository<DemoTask, Long> {

    Optional<DemoTask> findByTaskId(String taskId);

    void deleteByTaskId(String taskId);

    /**
     * Deletes records in the DemoTask table based on a list of taskIds.
     *
     * @param  taskIds    List of task ids to delete
     * @return           void
     */
    @Query("delete from DemoTask t where t.taskId in ?1")
    void deleteByTaskIds(List<String> taskIds);

    /**
     * Deletes records from DemoTask with the provided ids.
     *
     * @param  ids   list of ids to delete
     */
    @Query("delete from DemoTask t where t.id in ?1")
    void deleteByIds(List<Long> ids);

    /**
     * A description of the entire Java function.
     *
     * @param  pageable	description of parameter
     * @param  scheduleId	description of parameter
     * @return  Paged list of tasks
     */
    @Query("select t from DemoTask t where t.schedule.scheduleId = ?1")
    Page<DemoTask> findAll(Pageable pageable, String scheduleId);

    /**
     * A description of the entire Java function.
     *
     * @param  pageable	description of parameter
     * @param  cronSchedule	description of parameter
     * @return Paged list of tasks
     */
    @Query("select t from DemoTask t where t.schedule.cronSchedule = ?1")
    Page<DemoTask> findAllWithCron(Pageable pageable, String cronSchedule);

    /**
     * A description of the entire Java function.
     *
     * @param  pageable	description of parameter
     * @param  taskIds	description of parameter
     * @return         	description of return value
     */
    @Query("select t from DemoTask t where t.taskId in ?1")
    Page<DemoTask> findAllTaskWithTaskIds(Pageable pageable, List<String> taskIds);


}
