package com.creatix.domain.entity.store.account.device;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.PlatformType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(of = "id")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column
    private String name;

    @Column(nullable = false)
    @NotNull
    private String udid;

    @Column(nullable = false)
    @NotNull
    private PlatformType platform;

    @Column
    private String version;

    @Column
    private String pushToken;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @ManyToOne
    private Account account;

    @Transient
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @Transient
    public boolean getIsDeleted() {
        return this.isDeleted();
    }

}
