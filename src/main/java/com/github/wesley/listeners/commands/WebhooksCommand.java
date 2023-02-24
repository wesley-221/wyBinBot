package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebhooksCommand extends Command {
    Map<String, String> webhooksToCreate = new HashMap<>();

    public WebhooksCommand() {
        this.commandName = "webhooks";

        webhooksToCreate.put("matchfeed", "Used by **wyReferee**. \nRecommended settings in **wyReferee**: **Picks**, **Bans**, **Match result**. \nWill send a message in this channel when someone picks, bans or finishes a map.");
        webhooksToCreate.put("match-results", "Used by **wyReferee**. \nRecommended settings in **wyReferee**: **Final result**. \nWill send a message when a referee sends the final result through wyReferee.");
        webhooksToCreate.put("match-creation", "Used by **wyReferee**. \nRecommended settings in **wyReferee**: **Match creation**. \nWill send a message when a referee creates a match for the tournament.");
        webhooksToCreate.put("reschedules", "Used by **wyBin**: Rescheduling webhooks - For everyone. \nWill send a message when a rescheduler has confirmed the reschedule that a participant/team has requested.");
        webhooksToCreate.put("incoming-reschedules", "Used by **wyBin**: Rescheduling webhooks - For rschedulers. \nWill send a message when both participants/teams have confirmed their reschedule with each other.");
        webhooksToCreate.put("staff-registration", "Used by **wyBin**: Staff registration webhook. \nWill send a message when a user has used the staff registration form on the website.");
        webhooksToCreate.put("match-ping-staff", "Used by **wyBin**: Match ping webhook - Staff ping. \nWill send a message 30 minutes before a match is supposed to happen to remind them of it. \nGenerally used in the channel that is mainly used by the participants, such as #general.");
        webhooksToCreate.put("match-ping-participants", "Used by **wyBin**: Match ping webhook - participant ping. \nWill send a message 15 minutes before a match is supposed to happen to remind them of it. \nGenerally used in the channel that all staff members have access to, such as #general-staff.");
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        Optional<Server> optionalServer = interaction.getServer();

        optionalServer.ifPresent(server -> server
                .createChannelCategoryBuilder()
                .setName("wyBin webhooks")
                .create()
                .whenComplete((channelCategory, throwable) -> {
                    for (Map.Entry<String, String> webhook : webhooksToCreate.entrySet()) {
                        server
                                .createTextChannelBuilder()
                                .setName(webhook.getKey())
                                .setCategory(channelCategory)
                                .create()
                                .whenComplete((serverTextChannel, throwable1) -> {
                                    serverTextChannel
                                            .createWebhookBuilder()
                                            .setName(webhook.getKey())
                                            .create()
                                            .whenComplete((incomingWebhook, throwable2) -> {
                                                serverTextChannel
                                                        .sendMessage(webhook.getValue() + "\n\n**Webhook**: " + incomingWebhook.getUrl().toString());
                                            });
                                });
                    }

                    interaction
                            .createImmediateResponder()
                            .setContent("Successfully created a new category where all the channels will be created in. The channels with webhooks will be generated shortly.")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                }));
    }
}
