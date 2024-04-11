package com.nk.schedular.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import com.nk.schedular.model.Schedule;

import jakarta.transaction.Transactional;

public interface ScheduleRepo extends ListCrudRepository<Schedule, Long> , ListPagingAndSortingRepository<Schedule, Long> {

    
    Optional<Schedule> findByScheduleId(String scheduleId);

    /**
     * Deletes entries from Schedule table based on provided schedule IDs.
     *
     * @param  scheduleId  the schedule id to delete
     */
    @Modifying
    @Transactional
    @Query("delete from Schedule s where s.scheduleId = ?1")
    void deleteByScheduleId(String scheduleId);

    /**
     * Finds a schedule by cron schedule.
     *
     * @param  pageable     the pageable object for pagination
     * @param  cronSchedule the cron schedule to search for
     * @return              a page of schedules matching the cron schedule
     */
    @Query("select s from Schedule s where s.cronSchedule = ?1")
    Page<Schedule> findByCronSchedule(Pageable pageable, String cronSchedule);


    Boolean existsByScheduleId(String name);

}
