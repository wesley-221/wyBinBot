package com.github.wesley.repositories;

import com.github.wesley.models.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    Tournament findBySlug(String tournamentSlug);
}
