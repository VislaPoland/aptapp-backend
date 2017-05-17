package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Contact;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BusinessContact extends Contact {

    @Column
    private String street;
    @Column
    private String houseNumber;
    @Column
    private String zipCode;
    @Column
    private String city;
    @Column
    private String state;

}
