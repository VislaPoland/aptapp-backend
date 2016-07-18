package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class PageableDataResponse<T> extends DataResponse<T> {

    public PageableDataResponse(T data, Long pageSize, Long nextId) {
        super(data);
        this.pageSize = pageSize;
        this.nextId = nextId;
    }

    @ApiModelProperty(value = "Size of the page")
    private Long pageSize;
    @ApiModelProperty(value = "Next id where to start the search")
    private Long nextId;
}
