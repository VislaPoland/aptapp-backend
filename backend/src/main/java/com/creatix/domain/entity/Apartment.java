package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "unitNumber")
public class Apartment {
    @Id
    @Column(nullable = false)
    @NotNull
    private String unitNumber;

    @ManyToOne(optional = false)
    @JoinColumn
    private Property property;

    @OneToOne(mappedBy = "apartment")
    private Tenant tenant;

    @OneToOne
    @JoinColumn
    private Apartment aboveApartment;

    @OneToOne
    @JoinColumn
    private Apartment belowApartment;

    @OneToOne
    @JoinColumn
    private Apartment leftApartment;

    @OneToOne
    @JoinColumn
    private Apartment rightApartment;

    @OneToOne
    @JoinColumn
    private Apartment oppositeApartment;

    @OneToOne
    @JoinColumn
    private Apartment behindApartment;
}
