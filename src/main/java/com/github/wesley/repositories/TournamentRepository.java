package com.github.wesley.repositories;

import com.github.wesley.models.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    Tournament findBySlug(String tournamentSlug);

    Tournament findByDiscordSecret(String discordSecret);

    Tournament findByDiscordServerId(String serverId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Tournament SET discordServerId = :discordServerId, discordPlayerRoleId = :discordPlayerRoleId WHERE id = :tournamentId")
    void updateDiscordServerAndPlayerRoleId(Long tournamentId, String discordServerId, String discordPlayerRoleId);
}
