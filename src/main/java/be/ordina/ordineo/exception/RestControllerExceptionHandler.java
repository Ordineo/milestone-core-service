package be.ordina.ordineo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//import org.resthub.common.model.RestError;
import org.springframework.hateoas.VndErrors;
/**
 * Created by shbe on 01/06/16.
 */
@Slf4j
@ControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value={

            EntityNotFoundException.class,
            IllegalArgumentException.class

    })

    public ResponseEntity<Object> handleCustomException(Exception ex, WebRequest request) {

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        }else {
            log.warn("Unknown exception type: " + ex.getClass().getName());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
        return handleExceptionInternal(ex, buildRestError(ex, status), headers, status, request);//buildRestError(ex, status) this is object body
    }
    private VndErrors buildRestError(Exception ex, HttpStatus status) {
        //RestError.Builder builder = new RestError.Builder();
       // builder.setCode(status.value()).setStatus(status.getReasonPhrase()).setThrowable(ex);
        //return builder.build();
        return new VndErrors("Error!", ex.getMessage());
    }
}
