package org.example.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    BAD_REQUEST("COM40001", "Bad Request"),
    NOT_FOUND_REQUEST("COM40401", "Not Found Request"),
    METHOD_NOT_ALLOWED("COM40501", "Method Not Allowed"),
    INVALID_TYPE_VALUE("COM40002", "Invalid Type Value"),
    NO_SUCH_FIELD("COM40003", "No such filed"),

    INTERNAL_SERVER_ERROR("COM50001", "Internal Server Error"),
    NOT_FOUND_DATA("COM50002", "Not Found data"),
    DUPLICATED_DATA("COM5003","Duplicated data"),
    NULL_POINTER_ERROR("COM50099", "Null Pointer Error"),
    UNKNOWN_ERROR("COM50099", "Unknown error has occurred"),

    // Auth
    UNAUTHORIZED("AUT40101", "Unauthorized"),
    ACCESS_DENIED("AUT40301", "Access is Denied"),

    // SQL
    DB_CONNECTION_ERROR("SQL50000", "Database Connection Error"),
    DB_EXECUTE_ERROR("SQL50001", "Database Execute Error"),
    DB_VALIDATOR_ERROR("SQL50002", "Database Validator Error")
    ;


    private final String code;
    private final String title;
    private final String msg;

    ErrorCode(final String code, final String title) {
        this.code = code;
        this.title = title;
        this.msg = "";
    }

    ErrorCode(final String code, final String title, final String detail) {
        this.title = title;
        this.code = code;
        this.msg = detail;
    }
}
