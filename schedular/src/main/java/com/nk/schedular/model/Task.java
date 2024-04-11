package com.nk.schedular.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "native", sequenceName = "task_id_seq", allocationSize = 50, initialValue = 1000)
public abstract class Task<T> extends BaseModel implements Runnable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    private Long id;
    @Column(name = "task_id",nullable = false)
    private String taskId;
    private String description;
    @Column(name = "is_schedular_enabled")
    private Boolean isSchedularEnabled;

    @ManyToOne
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;
    
    public abstract T getData();

}
