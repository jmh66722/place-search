package org.example.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;


@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j
public class ControllerAspect {

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * 컨트롤러 로그 AOP
     * pointcut: controller 하위 패키지의 모든 method
    * */
    @Around(
            value = "execution(* org.example.*.controller..*.*(..)) " +
                    "&& !@annotation(org.example.aop.annotation.NoAspect)"
    )
    private Object doControllerAspect(ProceedingJoinPoint pjp) throws Throwable {

        //Request 로그
        this.requestLog(request);

        //Method 수행
        Object returnValue = pjp.proceed();

        //Response 로그{
        this.responseLog(request, returnValue);

        return returnValue;
    }


    /**
     * 예외 로그 AOP
     * pointcut: Controller 로직 내에서 실행되는 모든 exception (ExceptionHandler 로 핸들링된 모든 method)
    * */
    @Around(
            value = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler) " +
                    "&& !@annotation(org.example.aop.annotation.NoAspect)"
    )
    private Object doExceptionAspect(ProceedingJoinPoint pjp) throws Throwable {
        Object returnValue = pjp.proceed();

        Throwable throwable = (Throwable) pjp.getArgs()[0];

        //exception 로그
        this.exceptionLog(throwable);

        //Response 로그
        this.responseLog(this.request, returnValue);

        return returnValue;
    }




    //======================================================================================
    //=================== util method - START
    //======================================================================================
    public static String getBody(HttpServletRequest request) {
        String body;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    /**
     * Request 로그
     * @param request : 서블렛 request 요청값
     * */
    public void requestLog(HttpServletRequest request) {
        if(request==null) {
            return;
        }

        String body = getBody(request);

        StringBuilder requestDataString = new StringBuilder();
        if(body!=null && !body.isEmpty()){
            requestDataString.append(" - RequestBody: ");
            requestDataString.append(body);
        }
        if(request.getParameterMap()!=null && !request.getParameterMap().isEmpty()){
            requestDataString.append(" - RequestParams: ");
            try {
                requestDataString.append(objectMapper.writeValueAsString(request.getParameterMap()));
            }catch (Exception e){
            }
        }

        if(log.isInfoEnabled()){
            log.info("Request: [{}] {} {} ({})", request.getMethod(), request.getRequestURI(), requestDataString, request.getCharacterEncoding());
        }
    }

    /**
     * Response 로그
     * @param request : 서블렛 request 요청값
     * @param returnValue : response value
     * */
    public void responseLog(HttpServletRequest request, Object returnValue) throws Throwable{
        if(request==null) {
            return;
        }

        String responseBody;
        if(returnValue instanceof ResponseEntity){
            responseBody = objectMapper.writeValueAsString(((ResponseEntity) returnValue).getBody());
        }else{
            responseBody = objectMapper.writeValueAsString(returnValue);
        }
        if(log.isInfoEnabled()) {
            log.info("Response: [{}] {} - Result: {}", request.getMethod(), request.getRequestURI(), responseBody);
        }
    }

    /**
     * Exception 로그
     * @param throwable : Exception
     * */
    public void exceptionLog(Throwable throwable){
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        if(stackTraceElements==null || stackTraceElements.length < 1){
            stackTraceElements = throwable.getCause().getStackTrace();
        }
        String methodName = stackTraceElements[0].getMethodName();
        String fileName = stackTraceElements[0].getFileName();
        int lineNumber = stackTraceElements[0].getLineNumber();

        //Exception 발생 위치와 message만 로깅
        log.error("({}:{}.{}) Message: {}", fileName, lineNumber, methodName, throwable.getMessage());
        
        // 상세로그 보기 위하여 추가
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(new StringWriter()));
        log.debug(sw.toString());
    }

    //======================================================================================
    //=================== util method - END
    //======================================================================================
}
