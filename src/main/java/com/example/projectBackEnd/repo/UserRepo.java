package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE user_name = ?1", nativeQuery = true)
    Optional<User> getAllById(String userName);

    long count();
}
