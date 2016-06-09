package com.creatix.domain.dto;

import io.swagger.annotations.ApiModelProperty;

public class DataResponse<T> {

    @ApiModelProperty(required = true)
    private T data;

    public DataResponse() {
    }

    public DataResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
