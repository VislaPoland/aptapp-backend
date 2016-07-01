package com.creatix.domain.entity;

import com.creatix.domain.entity.account.Tenant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Apartment {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String unitNumber;

    @Column
    private Integer floor;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private Property property;

    @OneToOne(mappedBy = "apartment", fetch = FetchType.LAZY)
    private Tenant tenant;

    @Embedded
    @NotNull
    private ApartmentNeighbors neighbors;
}
