package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.models.tournament.TournamentTeamMember;
import com.github.wesley.repositories.TournamentRepository;
import com.github.wesley.repositories.TournamentTeamMemberRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.github.wesley.listeners.commands.VerifyCommand.PLAYER_ROLE;

@Component
public class PlayerRolesCommand extends Command {
    private final TournamentRepository tournamentRepository;
    private final TournamentTeamMemberRepository tournamentTeamMemberRepository;

    @Autowired
    public PlayerRolesCommand(TournamentRepository tournamentRepository, TournamentTeamMemberRepository tournamentTeamMemberRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentTeamMemberRepository = tournamentTeamMemberRepository;

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
                List<Role> roleList = server
                        .get()
                        .getRolesByName(PLAYER_ROLE);

                if (roleList.size() > 0) {
                    Role role = roleList.get(0);

                    List<TournamentTeamMember> allTeamMembers = this.tournamentTeamMemberRepository.getPlayersByTournamentId(tournament.getId());

                    for (TournamentTeamMember teamMember : allTeamMembers) {
                        interaction
                                .getApi()
                                .getUserById(teamMember.getDiscordId())
                                .whenComplete((user, throwable) -> {
                                    if (!user.getRoles(server.get()).contains(role)) {
                                        user.addRole(role);
                                    }

                                    user.updateNickname(server.get(), teamMember.getUser().getUsername().trim());
                                });
                    }

                    interaction
                            .createImmediateResponder()
                            .setContent("Updated everyone's roles and usernames if they were not present.")
                            .respond();
                }
            }
        }
    }
}
