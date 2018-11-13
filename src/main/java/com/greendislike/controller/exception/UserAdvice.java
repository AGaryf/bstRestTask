package com.greendislike.controller.exception;

import com.greendislike.controller.exception.type.UserNotEqualsException;
import com.greendislike.controller.exception.type.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserAdvice {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundHandler(UserNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UserNotEqualsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userNotEqualsHandler(UserNotEqualsException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

}
