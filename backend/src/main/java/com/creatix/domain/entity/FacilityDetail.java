package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class FacilityDetail {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    private Facility facility;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String value;

    @Column(nullable = false)
    @NotNull
    private Integer ordinal;

}
