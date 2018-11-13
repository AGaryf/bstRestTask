package com.greendislike.controller;

import com.greendislike.controller.exception.type.UserNotEqualsException;
import com.greendislike.controller.types.UserSearch;
import com.greendislike.persistence.model.User;

import com.greendislike.controller.exception.type.UserNotFoundException;
import com.greendislike.controller.types.UserResponse;
import com.greendislike.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCrypt;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    List<User> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping("/user")
    UserResponse createNewUser(@RequestBody User newUser) {
        newUser.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
        userService.add(newUser);
        return new UserResponse("User " + newUser.getFirstName() + " " + newUser.getLastName() + " with id = " + newUser.getId() + " successfully created");
    }

    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id) {
        return userService.getById(id)
                .orElseGet(() -> {
                    throw new UserNotFoundException(id);
                });
    }

    @PutMapping("/user/{id}")
    UserResponse replaceUser(@RequestBody User newUser, @PathVariable Long id){

        userService.getById(id)
            .map(user -> {
                if (user.getLastName().equals(newUser.getLastName())
                        && user.getFirstName().equals(newUser.getFirstName())) {
                    user.setEmail(newUser.getEmail());
                    user.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
                    return userService.edit(user);
                } else {
                    throw new UserNotEqualsException(user, newUser);
                }
            })
            .orElseGet(() -> {
                throw new UserNotFoundException(id);
            });

        return new UserResponse("User " + newUser.getFirstName() + " " + newUser.getLastName() + " with id = " + id + " successfully edited");
    }

    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    @PostMapping("/user/searches")
    User findByEmail(@RequestBody UserSearch userSearch) {
        return userService.getByEmail(userSearch.getEmail());
    }

}
