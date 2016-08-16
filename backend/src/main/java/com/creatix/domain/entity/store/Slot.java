package com.creatix.domain.entity.store;

import com.creatix.domain.enums.AudienceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
                @Index(columnList = "property_id"),
        }
)
@BatchSize(size = 80)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "beginTime", "endTime"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Property property;
    @NotNull
    @Column(nullable = false)
    private OffsetDateTime beginTime;
    @NotNull
    @Column(nullable = false)
    private OffsetDateTime endTime;
    @Column(nullable = false)
    private int unitDurationMinutes;
    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("offset ASC")
    private Set<SlotUnit> units;

    public void addUnit(@NotNull SlotUnit unit) {
        Objects.requireNonNull(unit, "Unit is null");

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

    public boolean removeUnit(@NotNull SlotUnit unit) {
        Objects.requireNonNull(unit, "Unit is null");

        boolean removed = false;
        if ( Objects.equals(this, unit.getSlot()) && (getUnits() != null) ) {
            removed = getUnits().remove(unit);
            if ( removed ) {
                unit.setSlot(null);
            }
        }
        return removed;
    }
}
