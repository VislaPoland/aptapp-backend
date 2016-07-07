package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"ownedProperties"})
public class PropertyOwner extends Account {

    @OneToMany(mappedBy = "owner")
    private Set<Property> ownedProperties;

    @Column
    private String website;
}
