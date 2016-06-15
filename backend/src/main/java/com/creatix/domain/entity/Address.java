package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Address {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String houseNumber;

    @Column(nullable = false)
    @NotNull
    private String streetName;

    /**
     * E, W, N, S
     */
    @Column
    private String compass;

    @Column(nullable = false)
    @NotNull
    private String town;

    @Column(nullable = false)
    @NotNull
    private String state;

    @Column(nullable = false)
    @NotNull
    private String zipCode;

    @Transient
    public String getFullAddress() {
        return String.format("%s %s %s, %s, %s %s", getHouseNumber(), getCompass(), getStreetName(), getTown(), getState(), getZipCode());
    }
}
