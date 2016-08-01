package com.creatix.domain.entity.store.account;

import com.creatix.domain.entity.store.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.Set;

@Entity
@BatchSize(size = 40)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"ownedProperties"})
public class PropertyOwner extends Account {

    @OneToMany(mappedBy = "owner")
    @OrderBy("name asc")
    private Set<Property> ownedProperties;

    @Column
    private String website;
}
