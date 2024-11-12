package com.github.wesley.repositories;

import com.github.wesley.models.VerifyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyUserRepository extends JpaRepository<VerifyUser, Integer> {
    VerifyUser findByVerificationCode(String verificationCode);
}
