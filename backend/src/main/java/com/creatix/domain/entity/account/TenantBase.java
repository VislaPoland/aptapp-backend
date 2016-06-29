
package com.creatix.domain.entity.account;

import com.creatix.domain.enums.TenantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBase extends Account {

    @Column
    @Enumerated(EnumType.STRING)
    private TenantType type;

}
