package com.greendislike.persistence.repository;

import com.greendislike.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    //@Query("select u from User u where u.email = :email")
    User findByEmail(@Param("email") String email);

}
