package org.example.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ErrorResult {

    @Schema(description = "응답 코드 번호")
    protected String code;

    @Schema(description = "응답 타이틀")
    protected String title;

    @Schema(description = "exception 메시지")
    protected String message;

    @Schema(description = "에러 내용")
    private Object errors;

    public ErrorResult() {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.title = ErrorCode.INTERNAL_SERVER_ERROR.getTitle();
        this.errors = null;
    }

    private ErrorResult(final ErrorCode code) {
        this.code = code.getCode();
        this.title = code.getTitle();
        this.errors = new ArrayList<>();
    }

    private ErrorResult(final ErrorCode code, final List<ErrorField> errors) {
        this.code = code.getCode();
        this.title = code.getTitle();
        this.errors = errors;
    }

    private ErrorResult(final ErrorCode code, final String errors) {
        this.code = code.getCode();
        this.title = code.getTitle();
        this.errors = errors;
    }

    public static ErrorResult of(final ErrorCode code) {
        return new ErrorResult(code);
    }

    public static ErrorResult of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResult(code, ErrorField.of(bindingResult));
    }

    public static ErrorResult of(final ErrorCode code, final List<ErrorField> errors) {
        return new ErrorResult(code, errors);
    }

    public static ErrorResult of(final ErrorCode code, String errors) {
        return new ErrorResult(code, errors);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ErrorField {
        private String field;
        private Object value;
        private String reason;

        private ErrorField(final String field, final Object value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<ErrorField> of(final String field, final Object value, final String reason) {
            List<ErrorField> fieldErrors = new ArrayList<>();
            fieldErrors.add(new ErrorField(field, value, reason));
            return fieldErrors;
        }

        private static List<ErrorField> of(final BindingResult bindingResult) {
            final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            return fieldErrors.stream()
                    .map(error -> new ErrorField(
                            error.getField(),
                            error.getRejectedValue() == null ? null : error.getRejectedValue(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
