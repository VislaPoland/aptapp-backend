package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.MaintenanceReservation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public class MaintenanceNotification extends Notification {

    @ManyToOne
    @JoinColumn
    @NotNull
    private Apartment targetApartment;

    @Column
    @NotNull
    private Boolean accessIfNotAtHome;

    @OneToOne(mappedBy = "notification")
    private MaintenanceReservation reservation;
}
