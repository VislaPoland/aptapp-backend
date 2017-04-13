package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.Contact;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
public class BusinessContact extends Contact {

    @Column
    private String street;
    @Column
    private String houseNumber;
    @Column
    private Integer zipCode;
    @Column
    private String country;
    @Column
    private String state;

}
