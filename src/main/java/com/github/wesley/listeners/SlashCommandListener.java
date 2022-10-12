package com.github.wesley.listeners;

import com.github.wesley.helper.RegisterListener;
import com.github.wesley.models.TournamentTeamMember;
import com.github.wesley.repositories.TournamentTeamMemberRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SlashCommandListener implements SlashCommandCreateListener, RegisterListener {
    private final TournamentTeamMemberRepository tournamentTeamMemberRepository;


    @Autowired
    public SlashCommandListener(TournamentTeamMemberRepository tournamentTeamMemberRepository) {
        this.tournamentTeamMemberRepository = tournamentTeamMemberRepository;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String commandName = interaction.getCommandName();

        if (commandName.equals("register")) {
            SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
            Optional<String> secret = interactionOption.getStringValue();

            if (secret.isPresent()) {
                TournamentTeamMember tournamentTeamMember = tournamentTeamMemberRepository.getByDiscordSecret(secret.get());

                if (tournamentTeamMember == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to finalize your registration. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                tournamentTeamMemberRepository.updateDiscordIdAndResetSecret(tournamentTeamMember.getId(), String.valueOf(interaction.getUser().getId()));

                interaction
                        .getUser()
                        .updateNickname(interaction.getServer().get(), tournamentTeamMember.getUser().getUsername())
                        .whenComplete((unused, throwable) -> interaction
                                .createImmediateResponder()
                                .setContent("You have successfully finalized your registration! Welcome " + tournamentTeamMember.getUser().getUsername() + ". \n\n**Note:** You have to refresh the wyBin website in order for the Discord step to show up as completed.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond());
            } else {
                interaction
                        .createImmediateResponder()
                        .setContent("Unable to finalize your registration. The secret you entered was invalid.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        }
    }
}
