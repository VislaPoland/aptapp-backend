package com.creatix.domain.entity.store;

import com.creatix.domain.enums.FacilityType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Facility {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Property property;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityType type;

    @Column
    private String name;

    @Column(length = 4096)
    private String description;

    @Column
    private String openingHours;

    @Column
    private String location;

}
