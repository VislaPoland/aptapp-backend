package com.creatix.domain.entity;

import com.creatix.domain.enums.PropertyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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

    @OneToOne(optional = false)
    @JoinColumn
    @NotNull
    private Address address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deleteDate;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private PropertyOwner owner;

    @OneToMany(mappedBy = "managedProperty")
    private List<PropertyManager> managers;

    @OneToMany(mappedBy = "property")
    private List<Facility> facilities;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Contact> contacts;
}
