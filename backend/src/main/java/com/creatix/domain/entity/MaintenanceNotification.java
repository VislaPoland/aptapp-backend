package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
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
}
