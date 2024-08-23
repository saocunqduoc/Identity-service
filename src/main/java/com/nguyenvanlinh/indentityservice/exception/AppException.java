package com.nguyenvanlinh.indentityservice.exception;

public class AppException extends RuntimeException {

    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        // kế thừa exception
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
