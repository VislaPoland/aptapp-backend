package com.creatix.domain.entity.store.business;

import com.creatix.domain.enums.CommunicationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
@EqualsAndHashCode(of = "id")
public class BusinessContact {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;
    @Column
    @Enumerated(EnumType.STRING)
    private CommunicationType communicationType;
    @Column(length = 25)
    private String communicationValue;
    @Column
    private String street;
    @Column(length = 10)
    private String houseNumber;
    @Column(length = 10)
    private String zipCode;
    @Column(length = 50)
    private String city;
    @Column(length = 50)
    private String state;

}
