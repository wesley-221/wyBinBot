package com.github.wesley.modules.verification.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscordVerification {
    private String wyBinTournamentSlug;

    private String discordServerId;
    private String discordUserId;

    @Override
    public String toString() {
        return "DiscordVerification{" +
                "wyBinTournamentSlug='" + wyBinTournamentSlug + '\'' +
                ", discordServerId='" + discordServerId + '\'' +
                ", discordUserId='" + discordUserId + '\'' +
                '}';
    }
}
