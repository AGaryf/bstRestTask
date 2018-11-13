package com.greendislike.controller.exception.type;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("Could not find user with Id = " + userId);
    }
}
