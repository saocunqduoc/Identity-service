package com.nguyenvanlinh.indentityservice.exception;

public enum ErrorCode {
    UNCATEGORIZED(9999, "Uncategorized error!"), // exception chưa được khai báo
    INVALID_KEY(1001,"Invalid message key!"), // enum key truyền vào không đúng
    USER_EXISTED(1002, "User already existed!"),
    USERNAME_INVALID(1003, "Username must be at least 6 characters!"),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters!"),
    USER_NOT_EXISTED(1005, "User not existed!"),
    UNAUTHENTICATED(1006, "Unauthenticated!"),
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
