package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    @Column(nullable = false)
    @NotNull
    private String town;

    @Column(nullable = false)
    @NotNull
    private String state;

    @Column(nullable = false)
    @NotNull
    private String zipCode;
}
