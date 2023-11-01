package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.User;
import com.github.wesley.models.tournament.TournamentStaff;
import com.github.wesley.models.tournament.TournamentTeam;
import com.github.wesley.models.tournament.TournamentTeamMember;
import com.github.wesley.repositories.TournamentStaffRepository;
import com.github.wesley.repositories.TournamentTeamMemberRepository;
import com.github.wesley.repositories.TournamentTeamRepository;
import com.github.wesley.repositories.UserRepository;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Component
public class VerifyCommand extends Command {
    public static final String TEAM_PREFIX = "team";
    public static final String PLAYER_PREFIX = "player";
    public static final String STAFF_PREFIX = "staff";
    public static final String VERIFY_PREFIX = "verify";

    public static final String PLAYER_ROLE = "Player";

    private final TournamentTeamRepository tournamentTeamRepository;
    private final TournamentTeamMemberRepository tournamentTeamMemberRepository;
    private final TournamentStaffRepository tournamentStaffRepository;
    private final UserRepository userRepository;

    @Autowired
    public VerifyCommand(TournamentTeamRepository tournamentTeamRepository, TournamentTeamMemberRepository tournamentTeamMemberRepository, TournamentStaffRepository tournamentStaffRepository, UserRepository userRepository) {
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.tournamentTeamMemberRepository = tournamentTeamMemberRepository;
        this.tournamentStaffRepository = tournamentStaffRepository;
        this.userRepository = userRepository;

        this.commandName = "verify";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> optionalSecret = interactionOption.getStringValue();

        if (optionalSecret.isPresent()) {
            String secret = optionalSecret.get();

            if (secret.startsWith(TEAM_PREFIX + "-")) {
                TournamentTeam team = tournamentTeamRepository.getByDiscordSecret(secret);

                if (team == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to create or update your team. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                Optional<Server> server = interaction.getServer();

                if (server.isPresent()) {
                    List<ChannelCategory> channelCategoryList = server
                            .get()
                            .getChannelCategoriesByName(team.getName());

                    // Category does exist, give them the role
                    if (channelCategoryList.size() > 0) {
                        List<Role> roleList = server
                                .get()
                                .getRolesByName(team.getName());

                        Role role = roleList.get(0);

                        interaction
                                .getUser()
                                .addRole(role);

                        interaction
                                .createImmediateResponder()
                                .setContent("You have successfully been given the role for the team `" + role.getName() + "`.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();
                    }
                    // Category does not exist, create role + everything else
                    else {
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
                                                        .setContent("Successfully created the role, text and voice channel for your team!\n\n**Note:** You have to refresh the wyBin website in order for the Discord step to show up as completed.")
                                                        .setFlags(MessageFlag.EPHEMERAL)
                                                        .respond();
                                            });
                                });
                    }
                }
            } else if (secret.startsWith(PLAYER_PREFIX + "-")) {
                TournamentTeamMember tournamentTeamMember = tournamentTeamMemberRepository.getByDiscordSecret(secret);

                if (tournamentTeamMember == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to finalize your registration. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                Optional<Server> server = interaction.getServer();

                if (server.isPresent()) {
                    List<Role> roleList = server
                            .get()
                            .getRolesByName(PLAYER_ROLE);

                    tournamentTeamMemberRepository.updateDiscordIdAndResetSecret(tournamentTeamMember.getId(), String.valueOf(interaction.getUser().getId()));

                    if (roleList.size() > 0) {
                        Role role = roleList.get(0);

                        interaction
                                .getUser()
                                .addRole(role);

                        interaction
                                .getUser()
                                .updateNickname(interaction.getServer().get(), tournamentTeamMember.getUser().getUsername())
                                .whenComplete((unused, throwable) -> interaction
                                        .createImmediateResponder()
                                        .setContent("You have successfully finalized your registration! Welcome " + tournamentTeamMember.getUser().getUsername() + ". \n\n**Note:** You have to refresh the wyBin website in order for the Discord step to show up as completed.")
                                        .setFlags(MessageFlag.EPHEMERAL)
                                        .respond());
                    } else {
                        // Create role
                        server
                                .get()
                                .createRoleBuilder()
                                .setName(PLAYER_ROLE)
                                .setMentionable(false)
                                .setDisplaySeparately(false)
                                .setColor(new Color(96, 125, 136))
                                .create()
                                .whenComplete((role, roleThrowable) -> {
                                    if (roleThrowable != null)
                                        roleThrowable.printStackTrace();

                                    // Add role to user
                                    interaction
                                            .getUser()
                                            .addRole(role);

                                    interaction
                                            .getUser()
                                            .updateNickname(interaction.getServer().get(), tournamentTeamMember.getUser().getUsername())
                                            .whenComplete((unused, throwable) -> interaction
                                                    .createImmediateResponder()
                                                    .setContent("You have successfully finalized your registration! Welcome " + tournamentTeamMember.getUser().getUsername() + ". \n\n**Note:** You have to refresh the wyBin website in order for the Discord step to show up as completed.")
                                                    .setFlags(MessageFlag.EPHEMERAL)
                                                    .respond());
                                });
                    }
                }
            } else if (secret.startsWith(STAFF_PREFIX + "-")) {
                TournamentStaff tournamentStaff = tournamentStaffRepository.getByDiscordSecret(secret);

                if (tournamentStaff == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to verify who you are. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                tournamentStaffRepository.updateDiscordId(tournamentStaff.getId(), String.valueOf(interaction.getUser().getId()));

                interaction
                        .getUser()
                        .updateNickname(interaction.getServer().get(), tournamentStaff.getUser().getUsername())
                        .whenComplete((unused, throwable) -> interaction
                                .createImmediateResponder()
                                .setContent("You have successfully verified who you are! Your username has been changed to " + tournamentStaff.getUser().getUsername() + ". \n\n**Note:** You have to refresh the wyBin website in order for the Discord step to show up as completed.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond());
            } else if (secret.startsWith(VERIFY_PREFIX + "-")) {
                User user = userRepository.getByDiscordSecret(secret);

                if (user == null) {
                    interaction
                            .createImmediateResponder()
                            .setContent("Unable to verify who you are. The secret you entered was invalid.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    return;
                }

                interaction
                        .getUser()
                        .updateNickname(interaction.getServer().get(), user.getUsername())
                        .whenComplete((unused, throwable) -> interaction
                                .createImmediateResponder()
                                .setContent("You have successfully verified who you are! Your username has been changed to " + user.getUsername() + ".")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond());
            }
        } else {
            interaction
                    .createImmediateResponder()
                    .setContent("Unable to verify who you are. The secret you entered was invalid.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }
}
