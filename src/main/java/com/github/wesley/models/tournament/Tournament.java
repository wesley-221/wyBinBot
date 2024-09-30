package com.github.wesley.models.tournament;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Tournament {
    @Id
    private Long id;
    private String slug;
    private String name;
    private String discordSecret;

    @OneToMany(mappedBy = "tournament", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<TournamentRole> roles = new ArrayList<>();
}
