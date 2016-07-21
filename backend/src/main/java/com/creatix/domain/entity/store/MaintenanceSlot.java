package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(indexes = {
    @Index(columnList = "schedule_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = { "schedule", "reservations" })
public class MaintenanceSlot extends Slot {

    @OneToMany(mappedBy = "slot")
    private List<MaintenanceReservation> reservations;
    @ManyToOne(fetch = FetchType.LAZY)
    private MaintenanceSlotSchedule schedule;


}
