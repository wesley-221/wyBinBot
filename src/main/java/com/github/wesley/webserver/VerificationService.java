package com.github.wesley.webserver;

import com.github.wesley.DiscordConfiguration;
import com.github.wesley.models.VerifyUser;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.models.tournament.TournamentTeam;
import com.github.wesley.repositories.TournamentRepository;
import com.github.wesley.repositories.TournamentTeamRepository;
import com.github.wesley.repositories.VerifyUserRepository;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Optional;

@Service
public class VerificationService {
    private final DiscordConfiguration discordConfiguration;

    private final TournamentRepository tournamentRepository;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final VerifyUserRepository verifyUserRepository;

    @Autowired
    public VerificationService(DiscordConfiguration discordConfiguration, TournamentRepository tournamentRepository, TournamentTeamRepository tournamentTeamRepository, VerifyUserRepository verifyUserRepository) {
        this.discordConfiguration = discordConfiguration;
        this.tournamentRepository = tournamentRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.verifyUserRepository = verifyUserRepository;
    }

    public void verifyUser(String verificationCode) {
        VerifyUser verifyUser = verifyUserRepository.findByVerificationCode(verificationCode);

        if (verifyUser == null) {
            return;
        }

        // Abort if nothing needs to be done to the user roles
        if (!verifyUser.getIsPlayer() && verifyUser.getTeamId() == null) {
            return;
        }

        Tournament findTournament = tournamentRepository.findBySlug(verifyUser.getTournamentSlug());

        if (findTournament == null) {
            return;
        }

        Optional<Server> server = discordConfiguration.getDiscordApi().getServerById(findTournament.getDiscordServerId());

        if (server.isEmpty()) {
            return;
        }

        Optional<Role> playerRole = server.get().getRoleById(findTournament.getDiscordPlayerRoleId());

        if (playerRole.isEmpty()) {
            return;
        }

        Optional<User> discordUser = server.get().getMemberById(verifyUser.getDiscordPlayerId());

        if (discordUser.isEmpty()) {
            return;
        }

        if (verifyUser.getIsPlayer()) {
            discordUser
                    .get()
                    .addRole(playerRole.get())
                    .whenComplete((unused, throwable) -> {
                        discordUser.get().sendMessage("You have been given the Player role for the tournament **" + findTournament.getName() + "**!");
                    });
        }

        TournamentTeam findTeam = tournamentTeamRepository.findById(verifyUser.getTeamId());

        if (findTeam != null) {
            if (findTeam.getDiscordId() == null) {
                server
                        .get()
                        .createRoleBuilder()
                        .setName(findTeam.getName())
                        .setMentionable(true)
                        .setDisplaySeparately(true)
                        .setColor(new Color(96, 125, 136))
                        .create()
                        .whenComplete((role, roleThrowable) -> {
                            if (roleThrowable != null)
                                roleThrowable.printStackTrace();

                            // Add role to user
                            discordUser.get().addRole(role);

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
                                                .setName(findTeam.getName())
                                                .setCategory(channelCategory)
                                                .create();

                                        // Create voice channel
                                        server
                                                .get()
                                                .createVoiceChannelBuilder()
                                                .setName(findTeam.getName())
                                                .setCategory(channelCategory)
                                                .create();

                                        tournamentTeamRepository.updateDiscordId(findTeam.getId(), String.valueOf(role.getId()));

                                        discordUser.get().sendMessage("You have been added to the team channels of **" + findTeam.getName() + "** for the tournament **" + findTournament.getName() + "**!");
                                    });
                        });

            } else {
                server
                        .get()
                        .getRoleById(findTeam.getDiscordId())
                        .ifPresent(role -> discordUser.get().addRole(role).whenComplete((unused, throwable) -> {
                            discordUser.get().sendMessage("You have been added to the team channels of **" + findTeam.getName() + "** for the tournament **" + findTournament.getName() + "**!");
                        }));
            }
        }
    }
}
