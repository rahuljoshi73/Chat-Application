package com.rahulapp.repository;

import com.rahulapp.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDtls,Integer> {
    UserDtls findByEmail(String email);
    UserDtls findByEmailAndEnabled(String email,boolean enabled);
    void deleteById(Long id);

    Optional<UserDtls> findById(Long id);

    @Query("select u from UserDtls u")
    List<UserDtls> findAll();
    @Query("select u from UserDtls u where u.verificationCode= ?1")
    UserDtls findByVerificationCode(String code);
}


