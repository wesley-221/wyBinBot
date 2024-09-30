package com.github.wesley.models;

import com.github.wesley.models.tournament.Tournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javacord.api.interaction.SlashCommandInteraction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SetupWyBinTournament {
    private SlashCommandInteraction slashCommandInteraction;
    private Tournament tournament;
}
