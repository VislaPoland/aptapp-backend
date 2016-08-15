package com.creatix.domain.entity.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Entity represents one time slot unit,
 */
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"slot_id", "offset"})
        },
        indexes = {
                @Index(columnList = "slot_id")
        })
@BatchSize(size = 80)
@Data
@EqualsAndHashCode(of = "id")
public class SlotUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Version
    private int version;
    @ManyToOne(optional = false)
    private Slot slot;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private int initialCapacity;
    @Column(nullable = false)
    private int offset;
    @ManyToMany(mappedBy = "units")
    private Set<MaintenanceReservation> reservations;

    @Transient
    public OffsetDateTime getBeginTime() {
        return getSlot().getBeginTime().plusMinutes(offset * slot.getUnitDurationMinutes());
    }

    @Transient
    public OffsetDateTime getEndTime() {
        return getBeginTime().plusMinutes(slot.getUnitDurationMinutes());
    }
}
