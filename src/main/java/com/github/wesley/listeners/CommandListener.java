package com.github.wesley.listeners;

import com.github.wesley.helper.RegisterListener;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CommandListener implements MessageCreateListener, RegisterListener {
    private final String discordCommandPrefix;
    private final String botName;
    private final String githubLink;
    private final String githubIssuesLink;

    @Autowired
    public CommandListener(@Value("${discord.command-prefix}") String discordCommandPrefix, @Value("${bot.name}") String botName, @Value("${bot.github}") String githubLink, @Value("${bot.issues}") String githubIssuesLink) {
        this.discordCommandPrefix = discordCommandPrefix;
        this.botName = botName;
        this.githubLink = githubLink;
        this.githubIssuesLink = githubIssuesLink;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        // Check if the author is not a bot
        if (messageCreateEvent.getMessageAuthor().isBotUser())
            return;

        // Check if it is being sent in as a private message
        if (!messageCreateEvent.isPrivateMessage())
            return;

        // Check if the message starts with the command prefix
        if (!messageCreateEvent.getMessage().getContent().startsWith(discordCommandPrefix))
            return;

        List<String> commandSplit = new ArrayList<>(Arrays.asList(messageCreateEvent.getMessage().getContent().substring(discordCommandPrefix.length()).split(" ")));
        String commandName = commandSplit.get(0);
        commandSplit.remove(0);

        if (commandName.equalsIgnoreCase("invite")) {
            String botInvite = messageCreateEvent
                    .getApi()
                    .createBotInvite(
                            new PermissionsBuilder()
                                    .setAllowed(PermissionType.CHANGE_NICKNAME)
                                    .setAllowed(PermissionType.MANAGE_NICKNAMES)
                                    .setAllowed(PermissionType.MANAGE_ROLES)
                                    .build());

            String embedDescription = "**Add " + botName + " to your Discord guild:** \n" +
                    botInvite + "\n\n" +
                    "**Bug/feature requests:** \n" +
                    "File an issue: " + githubLink + "\n\n" +
                    "**Source code:** \n" +
                    githubIssuesLink;

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setThumbnail(messageCreateEvent.getApi().getYourself().getAvatar().getUrl().toString())
                    .setTimestampToNow()
                    .setFooter(botName)
                    .setColor(new Color(53, 84, 171))
                    .setDescription(embedDescription);

            messageCreateEvent.getChannel().sendMessage(embedBuilder);
        }
    }
}
