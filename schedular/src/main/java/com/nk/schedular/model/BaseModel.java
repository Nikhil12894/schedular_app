package com.nk.schedular.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseModel {
    @Column(name = "created_by")
    private Long createdBy;

    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime createdAt;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @LastModifiedDate
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdatedAt;

}
