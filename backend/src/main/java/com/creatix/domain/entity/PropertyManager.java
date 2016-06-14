package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PropertyManager extends Account {
    @OneToOne(mappedBy = "manager")
    private Property managedProperty;

    @Column
    private String website;
}
