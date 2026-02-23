package com.github.wesley;

import com.github.wesley.helper.RegisterListener;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.GloballyAttachableListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DiscordConfiguration {
    private final ApplicationContext applicationContext;
    private final String discordToken;
    private DiscordApi discordApi;

    public DiscordConfiguration(ApplicationContext applicationContext, @Value("${discord.token}") String discordToken) {
        this.applicationContext = applicationContext;
        this.discordToken = discordToken;
    }

    @Bean
    public void startDiscordBot() {
        this.discordApi = new DiscordApiBuilder().setToken(discordToken).setAllIntents().login().join();

        initialize();
    }

    @Bean
    public DiscordApi getDiscordApi() {
        return this.discordApi;
    }

    private void initialize() {
        Map<String, RegisterListener> listeners = applicationContext.getBeansOfType(RegisterListener.class);

        createSlashCommands();

        // Register all listeners with RegisterListener marker interface
        for (RegisterListener listener : listeners.values()) {
            discordApi.addListener((GloballyAttachableListener) listener);
            log.info("Registered listener " + listener.getClass().getName());
        }

        log.info("Successfully initialized");
        log.info("Discord invite link: " + discordApi.createBotInvite(new PermissionsBuilder().setAllowed(PermissionType.ADMINISTRATOR).build()));
    }

    private void createSlashCommands() {
        SlashCommand.with("invite", "Sends a message with the invite link of this bot.")
                .setEnabledInDms(true)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("verify", "Verify who you are by changing your name to your osu! username.")
                .setEnabledInDms(false)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("howtoverify", "Shows a description on how to verify yourself for this Discord server.")
                .setEnabledInDms(false)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("setupstreamrole", "Creates a role that people can opt-in to to get a ping for when the livestream goes live.")
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("timestamp", "Creates a list with timestamps for the given time",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "time", "The time used for the timestamp. Format: 01:00, 12:00, 17:00, 20:30, etc.", true)
                        ))
                .setEnabledInDms(true)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("webhooks", "Creates several channels with webhooks used for both wyReferee and wyBin.")
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("playerroles", "Goes through all the verified players and gives them the Player role and update their username.",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "tournamentslug", "The slug of the tournament. Ex. `wybin.xyz/tournaments/axs-8th-edition/home` > `axs-8th-edition`", true)
                        ))
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("updateteamname", "Updates your team channel names to whatever it is set on wyBin.")
                .setEnabledInDms(false)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("setupserver", "Setup the Discord server so that verification works.",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "secret", "The secret that was given to you by wyBin on the Discord Integration section.", true)
                        ))
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.ADMINISTRATOR)
                .createGlobal(discordApi)
                .join();

//        SlashCommand.with("teamcolour", "Changes the colour of the role from your team.")
//                .setEnabledInDms(false)
//                .setDefaultEnabledForEveryone()
//                .createGlobal(discordApi)
//                .join();
    }
}
