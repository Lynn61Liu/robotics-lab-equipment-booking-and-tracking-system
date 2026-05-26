package com.CodeCrafters.se761.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String>{

    @Query("SELECT u FROM Users u WHERE u.name = :upi")
    Optional<Users> findByUpi(@Param("upi") String upi);

    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);

}