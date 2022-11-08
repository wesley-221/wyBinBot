package com.github.wesley.listeners;

import com.github.wesley.helper.RegisterListener;
import com.github.wesley.models.TournamentTeam;
import com.github.wesley.models.TournamentTeamMember;
import com.github.wesley.repositories.TournamentTeamMemberRepository;
import com.github.wesley.repositories.TournamentTeamRepository;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Component
public class SlashCommandListener implements SlashCommandCreateListener, RegisterListener {
    private final TournamentTeamMemberRepository tournamentTeamMemberRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

    public final static String WATCHER_ROLE_NAME = "Watcher";

    @Autowired
    public SlashCommandListener(TournamentTeamMemberRepository tournamentTeamMemberRepository, TournamentTeamRepository tournamentTeamRepository) {
        this.tournamentTeamMemberRepository = tournamentTeamMemberRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
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

        if (commandName.equals("setupstreamrole")) {
            Optional<Server> server = event
                    .getSlashCommandInteraction()
                    .getServer();

            if (server.isPresent()) {
                List<Role> watcherRoleList = server
                        .get()
                        .getRolesByName(WATCHER_ROLE_NAME);

                if (watcherRoleList.size() == 0) {
                    server
                            .get()
                            .createRoleBuilder()
                            .setName(WATCHER_ROLE_NAME)
                            .create()
                            .whenComplete((role, throwable) -> {
                                if (throwable != null) {
                                    interaction
                                            .createImmediateResponder()
                                            .setContent("Bot is missing permissions to create the Watcher role. Make sure the Bot has Manage Roles permission.")
                                            .setFlags(MessageFlag.EPHEMERAL)
                                            .respond();
                                }
                            });
                }

                new MessageBuilder()
                        .setContent("Click to get or remove the Watcher role to get pinged whenever the livestream goes live.")
                        .addComponents(
                                ActionRow.of(
                                        Button.success("giveRole", "Give Watcher role"),
                                        Button.danger("removeRole", "Remove Watcher role")
                                )
                        )
                        .send(interaction.getChannel().get())
                        .whenComplete((message, throwable) -> {
                            if (throwable != null) {
                                interaction
                                        .createImmediateResponder()
                                        .setContent("Bot is missing permissions to type in this channel.")
                                        .setFlags(MessageFlag.EPHEMERAL)
                                        .respond();

                                return;
                            }

                            interaction
                                    .createImmediateResponder()
                                    .setContent("Successfully setup Watcher role!")
                                    .setFlags(MessageFlag.EPHEMERAL)
                                    .respond();
                        });
            }
        }

        if (commandName.equals("team")) {
            SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
            Optional<String> secret = interactionOption.getStringValue();

            if (secret.isPresent()) {
                TournamentTeam team = tournamentTeamRepository.getByDiscordSecret(secret.get());

                if (team == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to create or update your team. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                Optional<Server> server = event
                        .getSlashCommandInteraction()
                        .getServer();

                if (server.isPresent()) {
                    List<Role> roleList = server
                            .get()
                            .getRolesByName(team.getName());

                    if (roleList.size() > 0) {
                        Role role = roleList.get(0);

                        interaction
                                .getUser()
                                .addRole(role);

                        interaction
                                .createImmediateResponder()
                                .setContent("You have successfully been given the role for the team `" + role.getName() + "`.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();
                    } else {
                        // Create role
                        server
                                .get()
                                .createRoleBuilder()
                                .setName(team.getName())
                                .setMentionable(true)
                                .setDisplaySeparately(true)
                                .setColor(new Color(96, 125, 136))
                                .create()
                                .whenComplete((role, roleThrowable) -> {
                                    if (roleThrowable != null)
                                        roleThrowable.printStackTrace();

                                    // Add role to user
                                    interaction.getUser().addRole(role);

                                    // Create channel category
                                    server
                                            .get()
                                            .createChannelCategoryBuilder()
                                            .setName(role.getName())
                                            .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build())
                                            .addPermissionOverwrite(role, new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL, PermissionType.MANAGE_MESSAGES).build())
                                            .create()
                                            .whenComplete((channelCategory, channelCategoryThrowable) -> {
                                                if (channelCategoryThrowable != null)
                                                    channelCategoryThrowable.printStackTrace();

                                                // Create text channel
                                                server
                                                        .get()
                                                        .createTextChannelBuilder()
                                                        .setName(team.getName())
                                                        .setCategory(channelCategory)
                                                        .create();

                                                // Create voice channel
                                                server
                                                        .get()
                                                        .createVoiceChannelBuilder()
                                                        .setName(team.getName())
                                                        .setCategory(channelCategory)
                                                        .create();

                                                tournamentTeamRepository.updateDiscordId(team.getId(), String.valueOf(role.getId()));

                                                interaction
                                                        .createImmediateResponder()
                                                        .setContent("Successfully created the role, text and voice channel for your team!")
                                                        .setFlags(MessageFlag.EPHEMERAL)
                                                        .respond();
                                            });
                                });
                    }
                }
            } else {
                interaction
                        .createImmediateResponder()
                        .setContent("Unable to create or update your team. The secret you entered was invalid.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        }
    }
}
