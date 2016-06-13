package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PropertyManager extends Account {
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "manager")
    @NotNull
    private Property managedProperty;
}
