package com.creatix.domain.entity;

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
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private Boolean active;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn
    @NotNull
    private Address address;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn
    @NotNull
    private PropertyOwner owner;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn
    @NotNull
    private PropertyManager manager;


}
