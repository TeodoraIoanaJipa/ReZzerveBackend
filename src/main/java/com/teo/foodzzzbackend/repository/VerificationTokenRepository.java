package com.teo.foodzzzbackend.repository;

import com.teo.foodzzzbackend.model.User;
import com.teo.foodzzzbackend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);

    void deleteByToken(String token);
}
