package com.creatix.domain.entity.store;

import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.enums.PropertyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nullable;
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
@Table(indexes = {
        @Index(columnList = "address_id"),
        @Index(columnList = "owner_id"),
        @Index(columnList = "schedule_id"),
})
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "name"})
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
    
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    @Nullable
    private PropertyLogo logo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deleteDate;

    @Column(nullable = false)
    @NotNull
    private String timeZone;

    @Column(length = 512)
    @Length(max = 512)
    private String payRentUrl;

    @ManyToOne(optional = false)
    @JoinColumn
    @NotNull
    private PropertyOwner owner;

    @Column(nullable = false)
    @NotNull
    private Boolean enableSms;

    @Nullable
    @Column
    private Integer lockoutHours;

    @Nullable
    @Column
    private Integer disruptiveComplaintHours;

    @Nullable
    @Column
    private Integer disruptiveComplaintThreshold;

    @Nullable
    @Column
    private Integer throttleFastMinutes;

    @Nullable
    @Column
    private Integer throttleSlowHours;

    @Nullable
    @Column
    private Integer throttleFastLimit;

    @Nullable
    @Column
    private Integer throttleSlowLimit;
    
    @Nullable
    private String mainColor;
    
    @Nullable
    private String backgroundColor;
    
    @Nullable
    private String textColor;
    
    @OneToMany(mappedBy = "managedProperty")
    @OrderBy("firstName asc, lastName asc")
    private Set<PropertyManager> managers;

    @OneToMany(mappedBy = "property")
    private Set<Facility> facilities;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Contact> contacts;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MaintenanceSlotSchedule schedule;

    @OneToMany(mappedBy = "property")
    private List<PropertyPhoto> photos = new ArrayList<>(1);

    @OneToMany(mappedBy = "property")
    private List<PredefinedMessage> predefinedMessages;

    @Column(nullable = false)
    private Boolean enableSmsEscalation;

    @Column(nullable = false)
    private Boolean enableEmailEscalation;

    @Transient
    public ZoneOffset getZoneOffset(LocalDateTime dt) {
        return getZoneId().getRules().getOffset(dt);
    }

    @Transient
    public ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }
}
