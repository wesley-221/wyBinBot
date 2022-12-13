package com.github.wesley;

import com.github.wesley.helper.Log;
import com.github.wesley.helper.RegisterListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.GloballyAttachableListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DiscordConfiguration {
    private final ApplicationContext applicationContext;
    private final String discordToken;
    private DiscordApi discordApi;

    @Autowired
    public DiscordConfiguration(ApplicationContext applicationContext, @Value("${discord.token}") String discordToken) {
        this.applicationContext = applicationContext;
        this.discordToken = discordToken;
    }

    @Bean
    public void startDiscordBot() {
        this.discordApi = new DiscordApiBuilder()
                .setToken(discordToken)
                .setAllIntents()
                .login()
                .join();

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
            Log.info("Registered listener " + listener.getClass().getName());
        }

        Log.info("Successfully initialized");
        Log.info("Discord invite link: " + discordApi.createBotInvite(
                new PermissionsBuilder()
                        .setAllowed(PermissionType.ADMINISTRATOR)
                        .build()));
    }

    private void createSlashCommands() {
        SlashCommand.with("register", "Finalize your registration for the wyBin tournament",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "secret", "The secret that was given to you when you registered for a wyBin tournament", true)
                        ))
                .setEnabledInDms(false)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("team", "Create or update your team in this server.",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "secret", "The secret that was given to you when you created a team for a wyBin tournament", true)
                        ))
                .setEnabledInDms(false)
                .setDefaultEnabledForEveryone()
                .createGlobal(discordApi)
                .join();

        SlashCommand.with("verify", "Verify who you are by changing your name to your osu! username.",
                        List.of(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "secret", "The secret that was given to you on wyBin Discord verify", true)
                        ))
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
    }
}
