package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class PageableWithTotalCountDataResponse<T> extends DataResponse<T> {

    public PageableWithTotalCountDataResponse(T data, Integer pageSize, Integer pageNumber, Integer totalRows) {
        super(data);
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalRows = totalRows;
    }

    @ApiModelProperty(value = "Size of the page")
    private Integer pageSize;
    @ApiModelProperty(value = "Page number (starting from 0)")
    private Integer pageNumber;
    @ApiModelProperty(value = "Total rows count")
    private Integer totalRows;

}
