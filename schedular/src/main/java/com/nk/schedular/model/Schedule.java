package com.nk.schedular.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(name = "schedule_seq",initialValue = 1000,allocationSize = 50,sequenceName = "SCHEDULER_SEQ")
@Table(name = "SCHEDULER",uniqueConstraints = {@UniqueConstraint(columnNames = "SCHEDULE_ID")})
public class Schedule extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "schedule_seq")
    private Long id;
    @Column(name = "schedule_id",nullable = false)
    private String scheduleId;
    @Column(name = "cron_schedule",nullable = false)
    private String cronSchedule;
    // @Column(name = "description")
    // private String description;
}
