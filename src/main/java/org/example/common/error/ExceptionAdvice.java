package org.example.common.error;

import lombok.extern.slf4j.Slf4j;
import org.example.common.error.exception.DuplicatedDataException;
import org.example.common.error.exception.NotFoundDataException;
import org.hibernate.QueryException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.webjars.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 요청을 찾을 수 없음
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResult> handleHttpException(NotFoundException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.NOT_FOUND_REQUEST,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * 데이터 조회 실패
     */
    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<ErrorResult> handleHttpException(NotFoundDataException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.NOT_FOUND_DATA,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 중복된 데이터
     */
    @ExceptionHandler(DuplicatedDataException.class)
    public ResponseEntity<ErrorResult> handleHttpException(DuplicatedDataException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.DUPLICATED_DATA,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * request contentType이 json/application일때, json request를 모델객체에 파싱할때 발생
     * 주로 @RequestBody 어노테이션에서 발생
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final HttpMessageNotReadableException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final MethodArgumentNotValidException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE, e.getBindingResult());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final BindException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final MethodArgumentTypeMismatchException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INVALID_TYPE_VALUE,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final HttpRequestMethodNotSupportedException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.METHOD_NOT_ALLOWED,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     *
     */
    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final NoSuchFieldException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.NO_SUCH_FIELD,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * DB 커넥션 실패시 처리
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final DataAccessResourceFailureException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.DB_CONNECTION_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 쿼리 실패시 처리
     */
    @ExceptionHandler(QueryException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final QueryException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.DB_EXECUTE_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Data dao 호출시 유효성 오류가 발생하면 처리
     * */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final DataIntegrityViolationException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.DB_VALIDATOR_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final ClassCastException e) {
        final ErrorResult response = ErrorResult.of(ErrorCode.INTERNAL_SERVER_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Null Point Exception
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResult> handleHttpException(final NullPointerException e) {
        ErrorResult response = ErrorResult.of(ErrorCode.NULL_POINTER_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 예기치 못한 모든 예외 처리, Null Point Exception 등등..
     * 직접 핸들링하지 않은 모든 예외처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleHttpException(final Exception e) {
        ErrorResult response = ErrorResult.of(ErrorCode.UNKNOWN_ERROR,e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
