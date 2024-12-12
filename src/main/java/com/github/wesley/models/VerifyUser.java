package com.github.wesley.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class VerifyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tournamentSlug;
    private String discordPlayerId;
    private String verificationCode;
    private Long userId;
    private Long teamId;
    private Boolean isPlayer;
    private Date expireAt;

    public VerifyUser(String tournamentSlug, String discordPlayerId, String verificationCode, Date expireAt) {
        this.tournamentSlug = tournamentSlug;
        this.discordPlayerId = discordPlayerId;
        this.verificationCode = verificationCode;
        this.expireAt = expireAt;
    }

    public Boolean getIsPlayer() {
        if (isPlayer == null) {
            return false;
        }

        return isPlayer;
    }
}
