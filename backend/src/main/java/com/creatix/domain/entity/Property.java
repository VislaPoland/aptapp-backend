package com.creatix.domain.entity;

import com.creatix.domain.enums.PropertyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Property {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PropertyStatus status;

    @Column
    private String additionalInformation;

    @OneToOne(optional = false)
    @JoinColumn
    @NotNull
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private PropertyOwner owner;

    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    @NotNull
    private PropertyManager manager;
}
