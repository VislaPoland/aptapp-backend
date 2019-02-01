package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class MaintenanceNotification extends Notification {

    @QueryInit("tenant")
    @ManyToOne
    private Apartment targetApartment;

    @Column
    private Boolean accessIfNotAtHome;

    @Column
    private OffsetDateTime respondedAt;

    @Column
    private OffsetDateTime closedAt;

    @Column
    private Boolean hasPet;

    @Length(max = 2048)
    @Column(length = 2048)
    private String petInstructions;

    @OneToMany(mappedBy = "notification")
    @OrderBy("createdAt ASC")
    private List<NotificationHistory> history;

    /**
     * More reservations for one notification are needed to enable us
     * to track reschedules and to see history of reservations.
     */
    @OneToMany(mappedBy = "notification")
    private List<MaintenanceReservation> reservations;

    public void addReservation(@NotNull MaintenanceReservation reservation) {
        Objects.requireNonNull(reservation, "Reservation is null");
        if (reservations == null) {
            reservations = new ArrayList<>();
        }

        reservation.setNotification(this);
        reservations.add(reservation);
    }
}
