package com.creatix.domain.entity.store;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventSlot extends Slot {
    @NotEmpty
    @Column
    private String title;
    @Column
    private String description;
}
