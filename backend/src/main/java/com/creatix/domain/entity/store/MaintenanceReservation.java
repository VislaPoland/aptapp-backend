package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.ManagedEmployee;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
public class MaintenanceReservation {
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
    private MaintenanceSlot slot;
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

    @OneToOne
    private MaintenanceNotification notification;

    @Transient
    public void addUnit(SlotUnit unit) {
        if ( units == null ) {
            units = new HashSet<>();
        }
        if ( (slot == null) && (unit.getSlot() instanceof MaintenanceSlot) ) {
            slot = (MaintenanceSlot) unit.getSlot();
        }

        units.add(unit);
    }

}
