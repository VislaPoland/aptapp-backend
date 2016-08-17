package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import com.querydsl.core.annotations.QueryInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(indexes = {
    @Index(columnList = "target_apartment_id")
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class MaintenanceNotification extends Notification {

    @QueryInit("tenant")
    @ManyToOne
    @NotNull
    private Apartment targetApartment;
    @Column
    @NotNull
    private Boolean accessIfNotAtHome;
    @OneToMany(mappedBy = "notification")
    private List<MaintenanceReservation> reservations;

    public void addReservation(@NotNull MaintenanceReservation reservation) {
        Objects.requireNonNull(reservation, "Reservation is null");
        if ( reservations == null ) {
            reservations = new ArrayList<>();
        }

        reservation.setNotification(this);
        reservations.add(reservation);
    }
}
