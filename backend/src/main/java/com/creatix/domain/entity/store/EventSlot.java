package com.creatix.domain.entity.store;


import com.creatix.domain.enums.AudienceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventSlot extends Slot {
    @NotEmpty
    @Column
    private String title;
    @Column(length = 2048)
    private String description;
    @Column
    private String location;
    @Enumerated(EnumType.STRING)
    @Column
    private AudienceType audience;

}
