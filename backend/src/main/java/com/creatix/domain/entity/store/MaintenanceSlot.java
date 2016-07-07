package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaintenanceSlot extends Slot {

    @OneToMany(mappedBy = "slot")
    private List<MaintenanceReservation> reservations;
    @ManyToOne(fetch = FetchType.LAZY)
    private MaintenanceSlotSchedule schedule;


}
