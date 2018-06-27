package com.creatix.domain.entity.store.business;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
public class BusinessCategory {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Override
    public int hashCode() {
        return this.id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BusinessCategory) && (((BusinessCategory) obj).getId()).equals(this.getId());
    }
}
