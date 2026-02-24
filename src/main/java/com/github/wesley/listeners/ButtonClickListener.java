package com.github.wesley.listeners;

import com.github.wesley.helper.RegisterListener;
import com.github.wesley.models.ServerWatcherRole;
import com.github.wesley.repositories.ServerWatcherRoleRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.github.wesley.listeners.commands.SetupStreamRoleCommand.WATCHER_ROLE_NAME;

@Component
public class ButtonClickListener implements org.javacord.api.listener.interaction.ButtonClickListener, RegisterListener {
    private final ServerWatcherRoleRepository serverWatcherRoleRepository;

    public ButtonClickListener(ServerWatcherRoleRepository serverWatcherRoleRepository) {
        this.serverWatcherRoleRepository = serverWatcherRoleRepository;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();

        if (buttonInteraction.getCustomId().equals("giveRole")) {
            buttonInteraction
                    .getServer()
                    .ifPresent(server -> {
                        Long roleId = getWatcherRoleId(server.getId());

                        // Fallback for old Watcher role interactions
                        if (roleId == null) {
                            List<Role> roleList = server.getRolesByName(WATCHER_ROLE_NAME);

                            if (roleList.size() > 0) {
                                roleList
                                        .get(0)
                                        .addUser(buttonInteraction.getUser())
                                        .whenComplete((unused, throwable) -> buttonInteraction
                                                .createImmediateResponder()
                                                .setContent("You now have the Watcher role!")
                                                .setFlags(MessageFlag.EPHEMERAL)
                                                .respond());
                            }
                        } else {
                            Optional<Role> roleList = server.getRoleById(roleId);

                            roleList.ifPresent(role -> role
                                    .addUser(buttonInteraction.getUser())
                                    .whenComplete((unused, throwable) -> buttonInteraction
                                            .createImmediateResponder()
                                            .setContent("You now have the " + role.getName() + " role!")
                                            .setFlags(MessageFlag.EPHEMERAL)
                                            .respond()));
                        }
                    });
        }

        if (buttonInteraction.getCustomId().equals("removeRole")) {
            buttonInteraction
                    .getServer()
                    .ifPresent(server -> {
                        Long roleId = getWatcherRoleId(server.getId());

                        if (roleId == null) {
                            List<Role> roleList = server.getRolesByName(WATCHER_ROLE_NAME);

                            if (roleList.size() > 0) {
                                roleList
                                        .get(0)
                                        .removeUser(buttonInteraction.getUser())
                                        .whenComplete((unused, throwable) -> buttonInteraction
                                                .createImmediateResponder()
                                                .setContent("You no longer have the Watcher role!")
                                                .setFlags(MessageFlag.EPHEMERAL)
                                                .respond());
                            }
                        } else {
                            Optional<Role> roleList = server.getRoleById(roleId);

                            roleList.ifPresent(role -> role
                                    .removeUser(buttonInteraction.getUser())
                                    .whenComplete((unused, throwable) -> buttonInteraction
                                            .createImmediateResponder()
                                            .setContent("You no longer have the " + role.getName() + " role!")
                                            .setFlags(MessageFlag.EPHEMERAL)
                                            .respond()));
                        }
                    });
        }
    }

    private Long getWatcherRoleId(Long discordServerId) {
        Optional<ServerWatcherRole> serverWatcherRole = serverWatcherRoleRepository.findById(discordServerId);

        return serverWatcherRole.map(ServerWatcherRole::getWatcherRoleId).orElse(null);
    }
}
