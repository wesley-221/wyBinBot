package com.github.wesley.listeners.commands;

import com.github.wesley.helper.DiscordRoleHelper;
import com.github.wesley.models.Command;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.repositories.TournamentRepository;
import com.github.wesley.services.SetupWyBinDiscordService;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.wesley.services.SetupWyBinDiscordService.WYBIN_SETUP_ROLES_BUTTON;
import static com.github.wesley.services.SetupWyBinDiscordService.WYBIN_SETUP_WEBHOOKS_BUTTON;

@Component
public class SetupWybinCommand extends Command {
    private final SetupWyBinDiscordService setupWyBinDiscordService;
    private final TournamentRepository tournamentRepository;

    @Autowired
    public SetupWybinCommand(SetupWyBinDiscordService setupWyBinDiscordService, TournamentRepository tournamentRepository) {
        this.setupWyBinDiscordService = setupWyBinDiscordService;
        this.tournamentRepository = tournamentRepository;
        this.commandName = "setupwybin";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        List<Role> allRoles = interaction.getServer().get().getRoles();
        List<SelectMenuOption> roleList = new ArrayList<>();

        allRoles.forEach(role -> {
            roleList.add(SelectMenuOption.create(role.getName(), role.getIdAsString()));
        });
//
        DiscordRoleHelper tournamentHostHelper = new DiscordRoleHelper("Select the Discord role for Tournament hosts",
                ActionRow.of(SelectMenu.create("tournament-hosts", "Click here to show a list with all roles", 1, 1, roleList)));
//
//        DiscordRoleHelper streamerHelper = new DiscordRoleHelper("Select the Discord role for Streamers",
//                ActionRow.of(SelectMenu.create("streamers", "Click here to show a list with all roles", 1, 1, roleList)));
//
//        DiscordRoleHelper commentatorHelper = new DiscordRoleHelper("Select the Discord role for Commentators",
//                ActionRow.of(SelectMenu.create("commentators", "Click here to show a list with all roles", 1, 1, roleList)));
//
//        DiscordRoleHelper refereeHelper = new DiscordRoleHelper("Select the Discord role for Referees",
//                ActionRow.of(SelectMenu.create("referees", "Click here to show a list with all roles", 1, 1, roleList)));
//
//        DiscordRoleHelper schedulerHelper = new DiscordRoleHelper("Select the Discord role for Schedulers",
//                ActionRow.of(SelectMenu.create("schedulers", "Click here to show a list with all roles", 1, 1, roleList)));
//
//        DiscordRoleHelper mappoolerHelper = new DiscordRoleHelper("Select the Discord role for Mappoolers",
//                ActionRow.of(SelectMenu.create("mappoolers", "Click here to show a list with all roles", 1, 1, roleList)));


        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> optionalSecret = interactionOption.getStringValue();

        if (optionalSecret.isPresent()) {
            Tournament tournament = tournamentRepository.findByDiscordSecret(optionalSecret.get());

            if (tournament == null) {
                interaction
                        .createImmediateResponder()
                        .setContent("The secret you entered was invalid.")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }

            String webhookCustomId = setupWyBinDiscordService.createCustomId(WYBIN_SETUP_WEBHOOKS_BUTTON);
            String rolesCustomId = setupWyBinDiscordService.createCustomId(WYBIN_SETUP_ROLES_BUTTON);

            interaction
                    .createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setDescription("Select what you want to setup for " + tournament.getName())
                            .setColor(Color.GREEN)
                    )
                    .addComponents(ActionRow.of(
                            Button.primary(webhookCustomId, "Setup webhooks"),
                            Button.primary(rolesCustomId, "Setup roles")
                    ))
                    .respond();

            setupWyBinDiscordService.addButtonForTournament(webhookCustomId, tournament);
            setupWyBinDiscordService.addButtonForTournament(rolesCustomId, tournament);

//            tournament-axs-9th-edition-b1f54d38-faa4-40f6-81d1-098cf5a2db0e
        } else {
            interaction
                    .createImmediateResponder()
                    .setContent("Unable to verify who you are. The secret you entered was invalid.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }
}
