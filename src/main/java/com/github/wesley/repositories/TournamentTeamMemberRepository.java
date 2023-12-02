package com.github.wesley.repositories;

import com.github.wesley.models.tournament.TournamentTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TournamentTeamMemberRepository extends JpaRepository<TournamentTeamMember, Integer> {
    TournamentTeamMember getByDiscordSecret(String secret);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE TournamentTeamMember SET discordId = :discordId WHERE id = :teamMemberId")
    void updateDiscordId(Long teamMemberId, String discordId);

    @Query(value = "SELECT * FROM tournament_team_member WHERE tournament_id = :tournamentId", nativeQuery = true)
    List<TournamentTeamMember> getPlayersByTournamentId(Long tournamentId);
}
