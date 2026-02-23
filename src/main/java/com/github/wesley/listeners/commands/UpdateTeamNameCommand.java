package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.tournament.TournamentTeam;
import com.github.wesley.repositories.TournamentTeamRepository;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

@Component
public class UpdateTeamNameCommand extends Command {
    private final TournamentTeamRepository tournamentTeamRepository;

    public boolean teamUpdated;

    public UpdateTeamNameCommand(TournamentTeamRepository tournamentTeamRepository) {
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.commandName = "updateteamname";

        teamUpdated = false;
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        if (interaction.getChannel().isEmpty() || interaction.getChannel().get().asServerTextChannel().isEmpty()) {
            interaction
                    .createImmediateResponder()
                    .setContent("This can only be used within a Server.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        Server server = interaction.getServer().get();
        ServerTextChannel serverTextChannel = interaction.getChannel().get().asServerTextChannel().get();

        serverTextChannel.getOverwrittenRolePermissions().forEach((roleId, permissions) -> {
            if (permissions.getDeniedBitmask() == 0) {
                TournamentTeam findTeam = tournamentTeamRepository.findByDiscordId(String.valueOf(roleId));

                if (findTeam == null) {
                    return;
                }

                this.teamUpdated = true;

                server.getChannelCategories().forEach(channelCategory -> {
                    channelCategory.getOverwrittenPermissions().forEach((channelRoleId, channelPermission) -> {
                        if (permissions.getDeniedBitmask() == 0) {
                            if (roleId.equals(channelRoleId)) {
                                channelCategory.updateName(findTeam.getName());
                            }
                        }
                    });
                });

                server.getTextChannels().forEach(textChannel -> {
                    textChannel.getOverwrittenPermissions().forEach((textChannelRoleId, textChannelPermission) -> {
                        if (permissions.getDeniedBitmask() == 0) {
                            if (roleId.equals(textChannelRoleId)) {
                                textChannel.updateName(findTeam.getName());
                            }
                        }
                    });
                });

                server.getVoiceChannels().forEach(voiceChannel -> {
                    voiceChannel.getOverwrittenPermissions().forEach((voiceChannelRoleId, voiceChannelPermission) -> {
                        if (permissions.getDeniedBitmask() == 0) {
                            if (roleId.equals(voiceChannelRoleId)) {
                                voiceChannel.updateName(findTeam.getName());
                            }
                        }
                    });
                });

                if (server.getRoleById(roleId).isPresent()) {
                    server.getRoleById(roleId).get().updateName(findTeam.getName());
                }

                interaction
                        .createImmediateResponder()
                        .setContent("The channel names for your team have been updated to **" + findTeam.getName() + "**. \n\r**Note**: If you’ve performed this action more than twice in the last 10 minutes, the next update will only occur after 10 minutes.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        });

        if (!teamUpdated) {
            interaction
                    .createImmediateResponder()
                    .setContent("Could not find your team. Are you running this command in your team's channel?")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }
}
