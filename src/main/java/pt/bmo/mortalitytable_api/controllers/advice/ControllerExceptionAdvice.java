package pt.bmo.mortalitytable_api.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.bmo.mortalitytable_api.controllers.dto.ErrorMsg;
import pt.bmo.mortalitytable_api.exception.NotFoundException;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ResponseEntity handleIllegalArgumentException(final IllegalArgumentException exception) {

        exception.printStackTrace();
        log.error(exception.getMessage());

        final ErrorMsg errorMsg = ErrorMsg.builder()
                .msg(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.badRequest().body(errorMsg);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ResponseEntity handleRuntimeException(final RuntimeException exception) {

        exception.printStackTrace();
        log.error(exception.getMessage());

        final ErrorMsg errorMsg = ErrorMsg.builder()
                .msg(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.badRequest().body(errorMsg);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ResponseEntity notFoundException(final NotFoundException exception) {

        exception.printStackTrace();
        log.error(exception.getMessage());

        final ErrorMsg errorMsg = ErrorMsg.builder()
                .msg(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ResponseEntity methodArgumentNotValidException(final MethodArgumentNotValidException exception) {

        exception.printStackTrace();
        log.error(exception.getMessage());

        List<ErrorMsg> errorMsgs = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> ErrorMsg.builder()
                        .msg(fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .timestamp(System.currentTimeMillis())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsgs);
    }
}
