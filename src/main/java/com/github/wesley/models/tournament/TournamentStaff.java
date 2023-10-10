package com.github.wesley.models.tournament;

import com.github.wesley.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class TournamentStaff {
    @Id
    private Long id;

    private String discordId;
    private String discordSecret;

    @OneToOne
    private User user;
}
