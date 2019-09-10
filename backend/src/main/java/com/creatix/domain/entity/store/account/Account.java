package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "primaryEmail" })
@BatchSize(size = 80)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column
    private String primaryPhone;

    @Column(nullable = false)
    @NotNull
    @Email
    private String primaryEmail;

    @Column
    private String passwordHash;

    @Column
    @Email
    private String secondaryEmail;

    @Column
    private String secondaryPhone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountRole role;

    @Column(nullable = false)
    @NotNull
    private Boolean active;

    @Column(length = 128, unique = true)
    private String actionToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date actionTokenValidUntil;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private Set<Device> devices;

    @Column(nullable = false)
    private Boolean isNeighborhoodNotificationEnable = true;

    @Transient
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    @ManyToOne
    public Apartment apartment;

    @Transient
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @Transient
    public Property getProperty() {
        return null;
    }

    public static Comparator<Account> COMPARE_BY_FIRST_LAST_NAME = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(a.getFirstName()), StringUtils.lowerCase(b.getFirstName()))
            .append(StringUtils.lowerCase(a.getLastName()), StringUtils.lowerCase(b.getLastName()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_EMAIL = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(a.getPrimaryEmail()), StringUtils.lowerCase(b.getPrimaryEmail()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_UNIT = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(a.apartment == null?"":a.apartment.getUnitNumber()), StringUtils.lowerCase(b.apartment == null?"":b.apartment.getUnitNumber()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_STATUS = (a, b) -> new CompareToBuilder()
            .append(a.getActive(), b.getActive())
            .toComparison();
    
    
    public static Comparator<Account> COMPARE_BY_FIRST_LAST_NAME_DESC = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(b.getFirstName()), StringUtils.lowerCase(a.getFirstName()))
            .append(StringUtils.lowerCase(b.getLastName()), StringUtils.lowerCase(a.getLastName()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_EMAIL_DESC = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(b.getPrimaryEmail()), StringUtils.lowerCase(a.getPrimaryEmail()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_UNIT_DESC = (a, b) -> new CompareToBuilder()
            .append(StringUtils.lowerCase(b.apartment == null?"":b.apartment.getUnitNumber()),
            		StringUtils.lowerCase(a.apartment == null?"":a.apartment.getUnitNumber()))
            .toComparison();
    
    public static Comparator<Account> COMPARE_BY_STATUS_DESC = (a, b) -> new CompareToBuilder()
            .append(b.getActive(), a.getActive())
            .toComparison();
}
