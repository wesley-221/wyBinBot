package com.github.wesley.models.tournament;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class TournamentTeam {
    @Id
    private Long id;

    private String name;
    private String timezones;
    private String discordId;
    private String discordSecret;
    private String acronym;
    private String flagAcronym;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private List<TournamentTeamMember> teamMembers = new ArrayList<>();
}
