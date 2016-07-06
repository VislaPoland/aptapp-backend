package com.creatix.domain.entity.account;

import com.creatix.domain.entity.account.device.Device;
import com.creatix.domain.enums.AccountRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(of = "id")
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

    @Column(nullable = false)
    @NotNull
    private String companyName;

    @Column(nullable = false)
    @NotNull
    private String primaryPhone;

    @Column(nullable = false, unique = true)
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

    @OneToMany(mappedBy = "account")
    private Set<Device> devices;

    @Transient
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @Transient
    public boolean getIsDeleted() {
        return this.isDeleted();
    }

    @Transient
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }
}
