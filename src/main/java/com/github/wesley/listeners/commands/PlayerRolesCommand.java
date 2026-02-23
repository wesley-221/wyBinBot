package com.github.wesley.listeners.commands;

import com.github.wesley.helper.Log;
import com.github.wesley.models.Command;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.models.tournament.TournamentStaff;
import com.github.wesley.models.tournament.TournamentTeamMember;
import com.github.wesley.repositories.TournamentRepository;
import com.github.wesley.repositories.TournamentStaffRepository;
import com.github.wesley.repositories.TournamentTeamMemberRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlayerRolesCommand extends Command {
    private final TournamentRepository tournamentRepository;
    private final TournamentTeamMemberRepository tournamentTeamMemberRepository;
    private final TournamentStaffRepository tournamentStaffRepository;

    public PlayerRolesCommand(TournamentRepository tournamentRepository,
            TournamentTeamMemberRepository tournamentTeamMemberRepository,
            TournamentStaffRepository tournamentStaffRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentTeamMemberRepository = tournamentTeamMemberRepository;
        this.tournamentStaffRepository = tournamentStaffRepository;

        this.commandName = "playerroles";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> optionalTournamentSlug = interactionOption.getStringValue();

        if (optionalTournamentSlug.isPresent()) {
            String tournamentSlug = optionalTournamentSlug.get();

            Tournament tournament = this.tournamentRepository.findBySlug(tournamentSlug);

            if (tournament == null) {
                interaction
                        .createImmediateResponder()
                        .setContent("Tournament does not exist.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();

                return;
            }

            Optional<Server> server = interaction.getServer();

            if (server.isPresent()) {
                Optional<Role> playerRole = server
                        .get()
                        .getRoleById(tournament.getDiscordPlayerRoleId());

                if (playerRole.isPresent()) {
                    List<TournamentTeamMember> allTeamMembers = this.tournamentTeamMemberRepository
                            .getPlayersByTournamentId(tournament.getId());

                    for (TournamentTeamMember teamMember : allTeamMembers) {
                        if (teamMember.getDiscordId() == null) {
                            continue;
                        }

                        interaction
                                .getApi()
                                .getUserById(teamMember.getDiscordId())
                                .whenComplete((user, throwable) -> {
                                    if (throwable != null) {
                                        throwable.printStackTrace();
                                    }

                                    if (!user.getRoles(server.get()).contains(playerRole.get())) {
                                        user.addRole(playerRole.get()).exceptionally(ExceptionLogger.get());
                                    }

                                    user.updateNickname(server.get(), teamMember.getUser().getUsername().trim());
                                }).exceptionally(ExceptionLogger.get());
                    }
                }

                List<TournamentStaff> allStaff = this.tournamentStaffRepository.findByTournamentId(tournament.getId());

                for (TournamentStaff staffMember : allStaff) {
                    if (staffMember.getDiscordId() == null) {
                        continue;
                    }

                    interaction
                            .getApi()
                            .getUserById(staffMember.getDiscordId())
                            .whenComplete((user, throwable) -> {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }

                                user.updateNickname(server.get(), staffMember.getUser().getUsername().trim());

                                Log.info("Updated staff member " + staffMember.getUser().getUsername());
                            }).exceptionally(ExceptionLogger.get());
                }

                interaction
                        .createImmediateResponder()
                        .setContent("Updated everyone's roles and usernames if they were not present.")
                        .respond();
            }
        }
    }
}
