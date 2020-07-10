package com.superhao.weixin.qyapi.model;

import lombok.Data;

@Data
public class BizException extends RuntimeException {
    private int errorCode;
    private String message;

    public BizException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public BizException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
