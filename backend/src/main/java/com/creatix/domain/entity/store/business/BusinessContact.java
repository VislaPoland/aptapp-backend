package com.creatix.domain.entity.store.business;

import com.creatix.domain.enums.CommunicationType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
public class BusinessContact {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;
    @Column
    @Enumerated(EnumType.STRING)
    private CommunicationType communicationType;
    @Column
    private String communicationValue;
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
