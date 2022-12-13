package com.github.wesley.repositories;

import com.github.wesley.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getByDiscordSecret(String discordSecret);
}
