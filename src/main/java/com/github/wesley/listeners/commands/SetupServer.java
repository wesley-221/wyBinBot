package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.repositories.TournamentRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.github.wesley.listeners.commands.VerifyCommand.PLAYER_ROLE;

@Component
public class SetupServer extends Command {
    private final TournamentRepository tournamentRepository;

    @Autowired
    public SetupServer(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;

        this.commandName = "setupserver";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> optionalSecret = interactionOption.getStringValue();

        Server server = interaction.getServer().get();

        if (optionalSecret.isPresent()) {
            String secret = optionalSecret.get();
            Tournament findTournament = tournamentRepository.findByDiscordSecret(secret);

            if (findTournament == null) {
                interaction
                        .createImmediateResponder()
                        .setContent("The secret you entered was invalid.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            List<Role> playerRoles = server.getRolesByName(PLAYER_ROLE);

            if (playerRoles.size() > 0) {
                Role playerRole = playerRoles.get(0);

                tournamentRepository.updateDiscordServerAndPlayerRoleId(findTournament.getId(), server.getIdAsString(), playerRole.getIdAsString());

                interaction
                        .createImmediateResponder()
                        .setContent("Successfully setup the Discord server. The role " + playerRole.getMentionTag() + " was found and reused. \n\r**Note:** You have to refresh the wyBin website in order for the Discord settings to be updated.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            } else {
                server
                        .createRoleBuilder()
                        .setName(PLAYER_ROLE)
                        .setMentionable(false)
                        .setDisplaySeparately(false)
                        .setColor(new Color(96, 125, 136))
                        .create()
                        .whenComplete((role, throwable) -> {
                            if (throwable != null)
                                throwable.printStackTrace();

                            tournamentRepository.updateDiscordServerAndPlayerRoleId(findTournament.getId(), server.getIdAsString(), role.getIdAsString());

                            interaction
                                    .createImmediateResponder()
                                    .setContent("Successfully setup the Discord server. The role " + role.getMentionTag() + " has been created. \n\r**Note:** You have to refresh the wyBin website in order for the Discord settings to be updated.")
                                    .setFlags(MessageFlag.EPHEMERAL)
                                    .respond();
                        });
            }
        } else {
            interaction
                    .createImmediateResponder()
                    .setContent("The secret you entered was invalid.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }
}
