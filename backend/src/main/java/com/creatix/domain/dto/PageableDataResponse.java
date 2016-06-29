package com.creatix.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableDataResponse<T> extends DataResponse<T> {
    public PageableDataResponse(Long pageSize, Long totalItems, Long totalPages, Long pageNumber) {
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
    }

    public PageableDataResponse(T data, Long pageSize, Long totalItems, Long totalPages, Long pageNumber) {
        super(data);
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
    }

    @ApiModelProperty(value = "Size of the page")
    private Long pageSize;
    @ApiModelProperty(value = "Total count of elements")
    private Long totalItems;
    @ApiModelProperty(value = "Total count of pages")
    private Long totalPages;
    @ApiModelProperty(value = "Serial number of the page")
    private Long pageNumber;
}
