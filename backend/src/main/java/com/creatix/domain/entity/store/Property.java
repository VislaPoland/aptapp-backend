package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.enums.PropertyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "name" })
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

    @Column(nullable = false)
    @NotNull
    private Boolean enableSms;

    @OneToMany(mappedBy = "managedProperty")
    private Set<PropertyManager> managers;

    @OneToMany(mappedBy = "property")
    private Set<Facility> facilities;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Contact> contacts;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MaintenanceSlotSchedule schedule;

    @OneToMany(mappedBy = "property")
    private List<PropertyPhoto> photos = new ArrayList<>(1);

    @Transient
    public ZoneOffset getZoneOffset(LocalDateTime dt) {
        return getZoneId().getRules().getOffset(dt);
    }

    @Transient
    private ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }
}
