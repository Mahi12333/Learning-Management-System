package com.maven.neuto.repository;


import com.maven.neuto.model.Otpverify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerifyRepository extends JpaRepository<Otpverify, Long> {
    Optional<Otpverify> findByEmail(String email);
}
