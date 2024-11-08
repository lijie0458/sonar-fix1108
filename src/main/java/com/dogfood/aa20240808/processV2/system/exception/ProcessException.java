package com.dogfood.aa20240808.processV2.system.exception;

public class ProcessException extends RuntimeException {

    private String message;
    private String code;

    public ProcessException(String message) {
        this.message = message;
        this.code = "500";
    }

    public ProcessException(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public ProcessException(String message, Throwable t) {
        super(t);
        this.message = message;
        this.code = "500";
    }

    public ProcessException(String message, String code, Throwable t) {
        super(t);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
