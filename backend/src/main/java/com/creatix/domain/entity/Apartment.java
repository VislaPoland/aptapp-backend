package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "unitNumber")
public class Apartment {
    @Id
    @Column(nullable = false)
    @NotNull
    private String unitNumber;

    @OneToOne(mappedBy = "apartment")
    private Tenant tenant;
}
