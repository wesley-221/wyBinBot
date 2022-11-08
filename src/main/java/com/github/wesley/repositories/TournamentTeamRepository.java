package com.github.wesley.repositories;

import com.github.wesley.models.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Integer> {
    TournamentTeam getByDiscordSecret(String secret);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE TournamentTeam SET discordId = :discordId WHERE id = :teamId")
    void updateDiscordId(Long teamId, String discordId);
}
