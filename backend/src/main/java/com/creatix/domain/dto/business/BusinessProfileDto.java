package com.creatix.domain.dto.business;

import lombok.Data;

import java.util.List;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Data
public class BusinessProfileDto {

    private Long id;
    private String name;
    List<BusinessCategoryDto> businessCategoryList;
    private BusinessContactDto contact;
    private String description;

}
