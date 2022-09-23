package org.example.common.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * 처리되지 못한 exception 처리 및 error 처리를 여기서 처리
 * 기존 ExceptionAdvice 를 사용하기 위하여 return 없이 Exception 발생시킴
 */
@RestController
public class CommonErrorController implements ErrorController {
    @RequestMapping("/error")
    public void error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null) {
            int statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                throw new NotFoundException("Not found request mapping");
            }
        }
    }
}
