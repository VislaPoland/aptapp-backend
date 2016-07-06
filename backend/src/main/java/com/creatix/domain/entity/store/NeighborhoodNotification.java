package com.creatix.domain.entity.store;

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
    @ManyToOne
    @JoinColumn
    @NotNull
    private Apartment targetApartment;
}
