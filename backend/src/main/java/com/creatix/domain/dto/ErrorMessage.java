package com.creatix.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */

@Data
@JsonSerialize
public class ErrorMessage {
    private String error;
    private String code;

    public ErrorMessage(String error, String code) {
        this.error = error;
        this.code = code;
    }
}
