package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class InviteCommand extends Command {
    private final String botName;
    private final String githubLink;
    private final String githubIssuesLink;

    @Autowired
    public InviteCommand(@Value("${bot.name}") String botName, @Value("${bot.github}") String githubLink, @Value("${bot.issues}") String githubIssuesLink) {
        this.commandName = "invite";

        this.botName = botName;
        this.githubLink = githubLink;
        this.githubIssuesLink = githubIssuesLink;
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        String botInvite = interaction
                .getApi()
                .createBotInvite(
                        new PermissionsBuilder()
                                .setAllowed(PermissionType.MANAGE_CHANNELS)
                                .setAllowed(PermissionType.MANAGE_ROLES)
                                .setAllowed(PermissionType.MANAGE_WEBHOOKS)
                                .setAllowed(PermissionType.CHANGE_NICKNAME)
                                .setAllowed(PermissionType.MANAGE_NICKNAMES)
                                .build());

        String embedDescription = "**Add " + botName + " to your Discord guild:** \n" +
                botInvite + "\n\n" +
                "**Bug/feature requests:** \n" +
                "File an issue: " + githubLink + "\n\n" +
                "**Source code:** \n" +
                githubIssuesLink;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setThumbnail(interaction.getApi().getYourself().getAvatar().getUrl().toString())
                .setTimestampToNow()
                .setFooter(botName)
                .setColor(new Color(53, 84, 171))
                .setDescription(embedDescription);

        interaction
                .createImmediateResponder()
                .addEmbed(embedBuilder)
                .respond();
    }
}
