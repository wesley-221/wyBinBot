package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.ServerWatcherRole;
import com.github.wesley.repositories.ServerWatcherRoleRepository;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SetupStreamRoleCommand extends Command {
    private final ServerWatcherRoleRepository serverWatcherRoleRepository;

    public final static String WATCHER_ROLE_NAME = "Watcher";
    public final static String WATCHER_ROLE_NAME_OPTION = "role-name";
    public final static String WATCHER_MESSAGE_OPTION = "content-message";
    public final static String WATCHER_GET_ROLE_MESSAGE_OPTION = "get-role";
    public final static String WATCHER_REMOVE_ROLE_MESSAGE_OPTION = "remove-role";
    public final static String WATCHER_ROLE_OPTION = "role";

    public SetupStreamRoleCommand(ServerWatcherRoleRepository serverWatcherRoleRepository) {
        this.serverWatcherRoleRepository = serverWatcherRoleRepository;
        this.commandName = "setupstreamrole";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        Optional<Server> server = interaction
                .getServer();

        if (server.isPresent()) {
            Optional<ServerWatcherRole> findServerWatcherRole = serverWatcherRoleRepository.findById(server.get().getId());

            SlashCommandInteractionOption roleNameOption = null;
            SlashCommandInteractionOption messageOption = null;
            SlashCommandInteractionOption giveRoleMessageOption = null;
            SlashCommandInteractionOption removeRoleMessageOption = null;
            SlashCommandInteractionOption roleOption = null;

            if (!interaction.getArguments().isEmpty()) {
                for (SlashCommandInteractionOption option : interaction.getArguments()) {
                    if (option.getName().equals(WATCHER_ROLE_NAME_OPTION)) {
                        roleNameOption = option;
                    }

                    if (option.getName().equals(WATCHER_MESSAGE_OPTION)) {
                        messageOption = option;
                    }

                    if (option.getName().equals(WATCHER_GET_ROLE_MESSAGE_OPTION)) {
                        giveRoleMessageOption = option;
                    }

                    if (option.getName().equals(WATCHER_REMOVE_ROLE_MESSAGE_OPTION)) {
                        removeRoleMessageOption = option;
                    }

                    if (option.getName().equals(WATCHER_ROLE_OPTION)) {
                        roleOption = option;
                    }
                }
            }

            // Create or update the database object
            if (roleOption != null) {
                ServerWatcherRole updatedRole;

                if (findServerWatcherRole.isEmpty()) {
                    updatedRole = new ServerWatcherRole();

                    updatedRole.setWatcherRoleId(roleOption.getRoleValue().get().getId());
                    updatedRole.setDiscordServerId(server.get().getId());
                }
                else {
                    updatedRole = findServerWatcherRole.get();
                    updatedRole.setWatcherRoleId(roleOption.getRoleValue().get().getId());
                }

                serverWatcherRoleRepository.save(updatedRole);
                findServerWatcherRole = serverWatcherRoleRepository.findById(server.get().getId());
            }

            String roleName = roleNameOption == null ? WATCHER_ROLE_NAME : roleNameOption.getStringValue().get();
            String message = messageOption == null ? getDefaultInteractionMessage(roleName) : messageOption.getStringValue().get();
            String getGiveRoleMessage = giveRoleMessageOption == null ? getGiveRoleInteractionMessage(roleName) : giveRoleMessageOption.getStringValue().get();
            String getRemoveRoleMessage = removeRoleMessageOption == null ? getRemoveRoleInteractionMessage(roleName) : removeRoleMessageOption.getStringValue().get();

            if (findServerWatcherRole.isEmpty()) {
                server
                        .get()
                        .createRoleBuilder()
                        .setName(roleName)
                        .create()
                        .whenComplete((role, throwable) -> {
                            if (throwable != null) {
                                interaction
                                        .createImmediateResponder()
                                        .setContent("Bot is missing permissions to create the Watcher role. Make sure the Bot has Manage Roles permission.")
                                        .setFlags(MessageFlag.EPHEMERAL)
                                        .respond();
                            } else {
                                ServerWatcherRole newRole = new ServerWatcherRole();

                                newRole.setDiscordServerId(server.get().getId());
                                newRole.setWatcherRoleId(role.getId());

                                serverWatcherRoleRepository.save(newRole);

                                createInteraction(interaction, message, getGiveRoleMessage, getRemoveRoleMessage);
                            }
                        });
            } else {
                server
                        .get()
                        .getRoleById(findServerWatcherRole.get().getWatcherRoleId())
                        .ifPresent(role -> {
                            role
                                    .createUpdater()
                                    .setName(roleName)
                                    .update()
                                    .whenComplete((updatedRole, throwable) -> {
                                        if (throwable == null) {
                                            createInteraction(interaction, message, getGiveRoleMessage, getRemoveRoleMessage);
                                        }
                                    });
                        });
            }
        }
    }

    private void createInteraction(SlashCommandInteraction interaction, String contentMessage, String giveRoleButtonMessage, String removeRoleButtonMessage) {
        new MessageBuilder()
                .setContent(contentMessage)
                .addComponents(
                        ActionRow.of(
                                Button.success("giveRole", giveRoleButtonMessage),
                                Button.danger("removeRole", removeRoleButtonMessage)
                        )
                )
                .send(interaction.getChannel().get())
                .whenComplete((message, throwable) -> {
                    if (throwable != null) {
                        interaction
                                .createImmediateResponder()
                                .setContent("Bot is missing permissions to type in this channel.")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();

                        return;
                    }

                    interaction
                            .createImmediateResponder()
                            .setContent("Successfully setup the role and interaction!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                });
    }

    private String getDefaultInteractionMessage(String roleName) {
        return "Click to get or remove the " + roleName + " role. This role will be used to ping everyone whenever the livestream goes live.";
    }

    private String getGiveRoleInteractionMessage(String roleName) {
        return "Get " + roleName + " role";
    }

    private String getRemoveRoleInteractionMessage(String roleName) {
        return "Remove " + roleName + " role";
    }
}
