package com.nk.schedular.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Slf4j
@Entity
@Table(name = "DEMO_TASK",uniqueConstraints = @UniqueConstraint(columnNames = "TASK_ID"))
public class DemoTask extends Task<String>{
    
    @Override
    public void run() {
        log.info(this.getData());
    }

    /**
     * A description of the entire Java function.
     *
     * @return         description of return value
     */
    @Override
    public String getData() {
        return "data for test"+this.getDescription();
    }

}
