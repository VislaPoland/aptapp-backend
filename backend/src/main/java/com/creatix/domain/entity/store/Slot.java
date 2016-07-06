package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity represents one time slot,
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "beginTime"),
                @Index(columnList = "endTime"),
        }
)
@Data
@EqualsAndHashCode(of = "id")
@FilterDefs({
        @FilterDef(name= Slot.SLOT_BEGIN_TIME_BETWEEN, parameters={
                @ParamDef(name="fromDt", type="java.util.Date" ),
                @ParamDef(name="toDt", type="java.util.Date" ),
        }),
        @FilterDef(name= Slot.SLOT_BEGIN_TIME_FROM, parameters={
                @ParamDef(name="fromDt", type="java.util.Date" )
        })
})
@BatchSize(size = 80)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Slot {
    public static final String SLOT_BEGIN_TIME_BETWEEN = "slotBeginTimeBetween";
    public static final String SLOT_BEGIN_TIME_FROM = "slotBeginTimeFrom";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Property property;
    @Column(nullable = false)
    private OffsetDateTime beginTime;
    @Column(nullable = false)
    private OffsetDateTime endTime;
    @JoinTable(
            indexes = {
                    @Index(columnList = "Slot_id"),
                    @Index(columnList = "amenities_id")
            }
    )
    @Column(nullable = false)
    private int unitDurationMinutes;
    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL)
    private Set<SlotUnit> units;
    @OneToMany(mappedBy = "slot")
    private Set<MaintenanceReservation> reservations;
    @ManyToOne(fetch = FetchType.LAZY)
    private MaintenanceSlotSchedule schedule;

    public void addUnit(SlotUnit unit) {
        if ( unit.getSlot() == null ) {
            unit.setSlot(this);
        }
        if ( !(Objects.equals(unit.getSlot(), this)) ) {
            throw new IllegalArgumentException("Unit is already assigned to another slot.");
        }
        if ( units == null ) {
            units = new HashSet<>();
        }

        units.add(unit);
    }
}
