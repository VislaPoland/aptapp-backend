package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class MaintenanceSlotSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    @OneToOne(mappedBy = "schedule")
    private Property property;

    @Column(nullable = false)
    private int unitDurationMinutes;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    private Set<DurationPerDayOfWeek> durationPerDayOfWeek;

    @Column(nullable = false)
    private int initialCapacity;
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    private Set<MaintenanceSlot> slots;
    
    public void addSlot(@NotNull MaintenanceSlot slot) {
        Objects.requireNonNull(slot, "Slot is null");
        if ( slots == null ) {
            slots = new HashSet<>();
        }
        slot.setSchedule(this);
        slots.add(slot);
    }

    public boolean removeSlot(@NotNull MaintenanceSlot slot) {
        Objects.requireNonNull(slot, "Slot is null");

        boolean removed = false;
        if ( Objects.equals(this, slot.getSchedule()) && (getSlots() != null) ) {
            removed = getSlots().remove(slot);
            if ( removed ) {
                slot.setSchedule(null);
            }
        }
        return removed;
    }

    @PreRemove
    private void preRemove() {
        if ( slots != null ) {
            slots.forEach(s -> s.setSchedule(null));
        }
    }
}
