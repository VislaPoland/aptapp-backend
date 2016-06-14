package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class NeighborhoodNotification extends Notification {
    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private Apartment targetApartment;
}
