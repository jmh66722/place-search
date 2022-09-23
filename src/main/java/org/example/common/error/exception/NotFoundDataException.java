package org.example.common.error.exception;


import org.example.common.error.ErrorCode;

public class NotFoundDataException extends RuntimeException {
    private ErrorCode code;

    public NotFoundDataException() { super(); }
    public NotFoundDataException(String msg) { super(msg); }
    public NotFoundDataException(Throwable t) { super(t); }
    public NotFoundDataException(String msg, Throwable t) {
        super(msg, t);
    }

    public NotFoundDataException(String msg, ErrorCode code) {
        super(msg);
        this.code = code;
    }
    public NotFoundDataException(ErrorCode code) {
        super(code.getMsg());
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}