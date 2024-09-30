package com.github.wesley.models.tournament;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class TournamentRole {
    @Id
    private Long id;
    private String name;
    private String overwriteName;
    private String discordId;

    @ManyToOne
    private Tournament tournament;
}
