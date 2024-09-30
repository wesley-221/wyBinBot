package com.github.wesley.listeners.discordsetup;

import com.github.wesley.helper.DiscordRoleHelper;
import com.github.wesley.helper.RegisterListener;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.services.SetupWyBinDiscordService;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.wesley.services.SetupWyBinDiscordService.*;

@Component
public class WyBinDiscordSecretListener implements SelectMenuChooseListener, ButtonClickListener, RegisterListener {
    private final SetupWyBinDiscordService setupWyBinDiscordService;

    // TODO: split up all the interactions to separate files

    @Autowired
    public WyBinDiscordSecretListener(SetupWyBinDiscordService setupWyBinDiscordService) {
        this.setupWyBinDiscordService = setupWyBinDiscordService;
    }

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        SelectMenuInteraction selectMenuInteraction = event.getSelectMenuInteraction();

        Tournament tournament = setupWyBinDiscordService.getDropdownForTournament(selectMenuInteraction.getCustomId());

        if (tournament == null) {
            selectMenuInteraction
                    .createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setDescription("This dropdown no longer works, please re-run the setup command.")
                            .setColor(Color.RED))
                    .respond();
            return;
        }

        if (setupWyBinDiscordService.isSetupRescheduleForEveryone(event.getSelectMenuInteraction().getCustomId())) {
            setupRescheduleForEveryoneWebhookDropdown(selectMenuInteraction, tournament);
        }

        if (event.getSelectMenuInteraction().getCustomId().equals("tournament-hosts")) {


//            event.getInteraction().asMessageComponentInteraction().get().getMessage()
//                    .createUpdater()
//                    .removeAllComponents()
//                    .setContent(streamerHelper.getDescription())
//                    .addComponents(streamerHelper.getActionrow())
//                    .applyChanges();
//
//            String roleId = event.getSelectMenuInteraction().asSelectMenuInteraction().get().getChosenOptions().get(0).getValue();
//
//            event.getInteraction().createImmediateResponder()
//                    .setContent("Successfully set the role for Tournament Hosts to <@&" + roleId + ">")
//                    .respond();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();

        Tournament tournament = setupWyBinDiscordService.getButtonForTournament(buttonInteraction.getCustomId());

        if (tournament == null) {
            buttonInteraction
                    .createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setDescription("This button no longer works, please re-run the setup command.")
                            .setColor(Color.RED))
                    .respond();
            return;
        }

        if (setupWyBinDiscordService.isSetupRolesButton(buttonInteraction.getCustomId())) {
            setupRolesButton(buttonInteraction, tournament);
        }

        if (setupWyBinDiscordService.isSetupWebhooksButton(buttonInteraction.getCustomId())) {
            setupWebhooksButton(buttonInteraction, tournament);
        }

        if (setupWyBinDiscordService.isSetupRescheduleForEveryone(buttonInteraction.getCustomId())) {
            setupRescheduleForEveryoneWebhookButton(buttonInteraction, tournament);
        }
    }

    /**
     * Get a list with all roles as a SelectMenu
     *
     * @param roles the roles to add to the SelectMenu
     * @return the roles
     */
    public List<SelectMenuOption> getRoleList(List<Role> roles) {
        List<SelectMenuOption> roleList = new ArrayList<>();

        roles.forEach(role -> {
            roleList.add(SelectMenuOption.create(role.getName(), role.getIdAsString()));
        });

        return roleList;
    }

    /**
     * Get a list with all text channels as a SelectMenu
     *
     * @param channels the text channels to add to the SelectMenu
     * @return the text channels
     */
    public List<SelectMenuOption> getTextChannelsList(List<ServerTextChannel> channels) {
        List<SelectMenuOption> textChannelList = new ArrayList<>();

        channels.forEach(textChannel -> {
            textChannelList.add(SelectMenuOption.create(textChannel.getName(), textChannel.getIdAsString()));
        });

        return textChannelList;
    }

    /**
     * The button to set up roles was pressed
     *
     * @param buttonInteraction the button that was pressed
     * @param tournament        the tournament
     */
    public void setupRolesButton(ButtonInteraction buttonInteraction, Tournament tournament) {
        buttonInteraction.createImmediateResponder()
                .setContent("Pressed roles button for tournament " + tournament.getName())
                .respond();
    }

    /**
     * The button to set up webhooks was pressed
     *
     * @param buttonInteraction the button that was pressed
     * @param tournament        the tournament
     */
    public void setupWebhooksButton(ButtonInteraction buttonInteraction, Tournament tournament) {
        String rescheduleForEveryoneCustomId = setupWyBinDiscordService.createCustomId(WYBIN_RESCHEDULE_EVERYONE);
        String rescheduleForSchedulersCustomId = setupWyBinDiscordService.createCustomId(WYBIN_RESCHEDULE_SCHEDULER);
        String staffRegistrationCustomId = setupWyBinDiscordService.createCustomId(WYBIN_STAFF_REGISTRATION);
        String matchPingForStaffCustomId = setupWyBinDiscordService.createCustomId(WYBIN_MATCH_PING_STAFF);
        String matchPingForParticipantsCustomId = setupWyBinDiscordService.createCustomId(WYBIN_MATCH_PING_PARTICIPANTS);

        buttonInteraction
                .createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setDescription("Select what webhook you would like to setup")
                        .setColor(Color.GREEN)
                )
                .addComponents(ActionRow.of(
                        Button.primary(rescheduleForEveryoneCustomId, "Reschedule - For everyone"),
                        Button.primary(rescheduleForSchedulersCustomId, "Reschedule - For schedulers"),
                        Button.primary(staffRegistrationCustomId, "Staff registration"),
                        Button.primary(matchPingForStaffCustomId, "Match ping - For staff"),
                        Button.primary(matchPingForParticipantsCustomId, "Match ping - For participants")
                ))
                .respond();

        setupWyBinDiscordService.addButtonsForTournament(List.of(rescheduleForEveryoneCustomId, rescheduleForSchedulersCustomId, staffRegistrationCustomId, matchPingForStaffCustomId, matchPingForParticipantsCustomId), tournament);
    }

    /**
     * The button to set up Reschedule - For everyone was pressed
     *
     * @param buttonInteraction the button that was pressed
     * @param tournament        the tournament
     */
    public void setupRescheduleForEveryoneWebhookButton(ButtonInteraction buttonInteraction, Tournament tournament) {
        List<SelectMenuOption> channelList = getTextChannelsList(buttonInteraction.getServer().get().getTextChannels());
        String rescheduleForEveryoneCustomId = setupWyBinDiscordService.createCustomId(WYBIN_RESCHEDULE_EVERYONE);

        DiscordRoleHelper rescheduleForEveryoneWebhook = new DiscordRoleHelper("Select the Discord channel where the reschedule confirmation will be sent to",
                ActionRow.of(SelectMenu.create(rescheduleForEveryoneCustomId, "Click here to show a list with all channels", 1, 1, channelList)));

        buttonInteraction
                .createImmediateResponder()
                .setContent(rescheduleForEveryoneWebhook.getDescription())
                .addComponents(rescheduleForEveryoneWebhook.getActionrow())
                .respond();

        setupWyBinDiscordService.addDropdownForTournament(rescheduleForEveryoneCustomId, tournament);
    }

    public void rescheduleForSchedulersWebhook(ButtonInteraction buttonInteraction, Tournament tournament) {

    }

    public void staffRegistrationWebhook(ButtonInteraction buttonInteraction, Tournament tournament) {

    }

    public void matchPingForStaffWebhook(ButtonInteraction buttonInteraction, Tournament tournament) {

    }

    public void matchPingForParticipantsWebhook(ButtonInteraction buttonInteraction, Tournament tournament) {

    }

    private void setupRescheduleForEveryoneWebhookDropdown(SelectMenuInteraction selectMenuInteraction, Tournament tournament) {
        
    }
}
// tournament-axs-9th-edition-b1f54d38-faa4-40f6-81d1-098cf5a2db0e