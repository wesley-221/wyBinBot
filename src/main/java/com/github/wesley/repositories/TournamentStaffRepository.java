package com.github.wesley.repositories;

import com.github.wesley.models.TournamentStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TournamentStaffRepository extends JpaRepository<TournamentStaff, Integer> {
    TournamentStaff getByDiscordSecret(String discordSecret);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE TournamentStaff SET discordId = :discordId WHERE id = :staffMemberId")
    void updateDiscordId(Long staffMemberId, String discordId);
}
