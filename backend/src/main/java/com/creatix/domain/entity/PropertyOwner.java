package com.creatix.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PropertyOwner extends Account {

    @OneToMany(mappedBy = "owner")
    private Set<Property> ownedProperties;

    @Column
    private String website;
}
