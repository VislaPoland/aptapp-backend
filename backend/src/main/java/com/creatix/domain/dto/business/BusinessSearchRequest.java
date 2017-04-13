package com.creatix.domain.dto.business;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by kvimbi on 13/04/2017.
 */
@Data
public class BusinessSearchRequest {

    private Long businessCategoryId;
    @NotNull
    private String name;

}
