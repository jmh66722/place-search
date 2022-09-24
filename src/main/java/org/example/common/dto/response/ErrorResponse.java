package org.example.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.example.common.dto.ErrorCode;

@Getter
@Setter
public class ErrorResponse extends CommonApiResponse {

    @Schema(description = "응답 코드 번호")
    protected String code;

    @Schema(description = "응답 타이틀")
    protected String title;

    @Schema(description = "응답 메시지")
    protected String msg;

    public ErrorResponse() {
        super(
                false,
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
        );
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.title = ErrorCode.INTERNAL_SERVER_ERROR.getTitle();
        this.msg = ErrorCode.INTERNAL_SERVER_ERROR.getMsg();
    }

    private ErrorResponse(final ErrorCode code) {
        super(
                false,
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
        );
        this.status = code.getStatus();
        this.code = code.getCode();
        this.title = code.getTitle();
        this.msg = code.getMsg();
    }

    private ErrorResponse(final ErrorCode code, String msg) {
        super(
                false,
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus()
        );
        this.status = code.getStatus();
        this.code = code.getCode();
        this.title = code.getTitle();
        this.msg = msg;
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, String msg) {
        return new ErrorResponse(code, msg);
    }
}
