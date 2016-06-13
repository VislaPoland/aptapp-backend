package com.creatix.domain.entity;

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
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(of = "id")
@BatchSize(size = 80)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private String phone;

    @Column(nullable = false, unique = true)
    @NotNull
    @Email
    private String email;

    @Column
    private String passwordHash;

    @Column
    private String additionalContactInfo;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VehicleInformation> vehicles;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 128, unique = true)
    private String actionToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date actionTokenValidUntil;

    @Transient
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }
}
