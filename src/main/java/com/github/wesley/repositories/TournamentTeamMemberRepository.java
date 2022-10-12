package com.github.wesley.repositories;

import com.github.wesley.models.TournamentTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TournamentTeamMemberRepository extends JpaRepository<TournamentTeamMember, Integer> {
    TournamentTeamMember getByDiscordSecret(String discordSecret);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE TournamentTeamMember SET discordId = :discordId, discordSecret = NULL WHERE id = :teamMemberId")
    void updateDiscordIdAndResetSecret(Long teamMemberId, String discordId);
}
