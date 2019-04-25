package org.donglight.watchdog.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 功能
 *
 * @author donglight
 * @date 2019/4/18
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Response<String> handleRuntimeException(RuntimeException e){
        e.printStackTrace();
        log.error("error {}",e.getMessage());
        return Response.exception(ResponseEnum.SERVER_INTERNAL_ERROR.getCode(),e.getMessage(),"");
    }

    /***
     * 404处理
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFountHandler(HttpServletRequest request, NoHandlerFoundException e){
        log.error("error {}",e.getMessage());
        Map<String, Object> result = new HashMap<>(3);
        result.put("code",HttpStatus.NOT_FOUND);
        result.put("message",HttpStatus.NOT_FOUND.getReasonPhrase());
        result.put("path",request.getRequestURI());
        return result;
    }
}
