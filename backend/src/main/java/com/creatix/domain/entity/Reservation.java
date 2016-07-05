package com.creatix.domain.entity;

import com.creatix.domain.entity.account.ManagedEmployee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity represents reservation,
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "slot_id"),
                @Index(columnList = "employee_id")
        }
)
@Data
@EqualsAndHashCode(of = "id")
@BatchSize(size = 80)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Version
    private int version;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ManagedEmployee employee;
    @ManyToMany
    @JoinTable(
            indexes = {
                    @Index(columnList = "reservations_id"),
                    @Index(columnList = "units_id")
            }
    )
    private Set<SlotUnit> units;
    @ManyToOne(optional = false)
    private Slot slot;
    @Column(nullable = false)
    private OffsetDateTime beginTime;
    @Column(nullable = false)
    private OffsetDateTime endTime;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private int durationMinutes;
    @Column(length = 2048)
    private String transferLog;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;
    @Column(unique = true)
    private String stripeChargeId;
    @Column(length = 2048)
    private String note;

    public void addUnit(SlotUnit unit) {
        if ( units == null ) {
            units = new HashSet<>();
        }

        units.add(unit);
    }
}
