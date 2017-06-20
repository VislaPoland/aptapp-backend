package com.creatix.domain.entity.store.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBase extends Account {

    @Column
    private Boolean isTacAccepted;
    @Column
    private Boolean isPrivacyPolicyAccepted;

}
