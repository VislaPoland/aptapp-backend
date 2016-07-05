package com.creatix.domain.entity;

import com.creatix.domain.enums.AccountRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class MaintenanceSlotSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(optional = false)
    private Property property;
    @Column(nullable = false)
    private LocalTime beginTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private int unitDurationMinutes;
    @Enumerated(EnumType.STRING)
    @Column
    private AccountRole targetRole;
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();
    @Column(nullable = false)
    private int initialCapacity;
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.PERSIST)
    private Set<Slot> slots;
    @Column(nullable = false)
    private String timeZone;

    public void addSlot(Slot slot) {
        if ( slots == null ) {
            slots = new HashSet<>();
        }
        slot.setSchedule(this);
        slots.add(slot);
    }

    @PreRemove
    private void preRemove() {
        if ( slots != null ) {
            slots.stream().forEach(s -> s.setSchedule(null));
        }
    }

    @Transient
    public ZoneOffset getZoneOffset(LocalDateTime dt) {
        return getZoneId().getRules().getOffset(dt);
    }

    @Transient
    public ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }
}
