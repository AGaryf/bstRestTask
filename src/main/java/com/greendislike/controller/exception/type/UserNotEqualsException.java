package com.greendislike.controller.exception.type;

import com.greendislike.persistence.model.User;

public class UserNotEqualsException extends RuntimeException {

    public UserNotEqualsException(User user, User newUser) {
        super("User with id = " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " not equals "
                + newUser.getFirstName() + " " + newUser.getLastName());
    }

}
