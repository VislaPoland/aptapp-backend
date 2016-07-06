package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaintenanceSlot extends Slot {

    @OneToMany(mappedBy = "slot")
    private Set<MaintenanceReservation> reservations;


}
