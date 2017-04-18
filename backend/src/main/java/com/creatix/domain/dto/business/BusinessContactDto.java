package com.creatix.domain.dto.business;

import lombok.Data;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@Data
public class BusinessContactDto {

    private String street;

    private String houseNumber;

    private Integer zipCode;

    private String country;

    private String state;
}
