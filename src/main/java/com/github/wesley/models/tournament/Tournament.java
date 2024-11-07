package com.github.wesley.models.tournament;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Tournament {
    @Id
    private Long id;
    private String slug;

    private String discordServerId;
    private String discordPlayerRoleId;

    private String discordSecret;
}
