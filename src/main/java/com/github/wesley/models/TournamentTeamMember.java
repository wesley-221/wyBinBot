package com.github.wesley.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class TournamentTeamMember {
    @Id
    private Long id;
    private Integer pp;
    private Integer rank;
    private String timezone;
    private String discordId;
    private String discordSecret;

    @OneToOne
    private User user;
}
