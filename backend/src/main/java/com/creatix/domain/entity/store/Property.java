package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.enums.PropertyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

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

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    @NotNull
    private Address address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deleteDate;

    @Column(nullable = false)
    @NotNull
    private String timeZone;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private PropertyOwner owner;

    @OneToMany(mappedBy = "managedProperty")
    private Set<PropertyManager> managers;

    @OneToMany(mappedBy = "property")
    private Set<Facility> facilities;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Contact> contacts;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private PropertySchedule schedule;
}
