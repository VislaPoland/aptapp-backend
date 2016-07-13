package com.creatix.domain.entity.store.account;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBase extends Account {

    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

}
