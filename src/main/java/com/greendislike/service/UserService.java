package com.greendislike.service;

import com.greendislike.persistence.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAll();
    User add(User user);
    User edit(User user);
    void delete(Long id);
    User getByEmail(String email);
    Optional<User> getById(Long id);

}
