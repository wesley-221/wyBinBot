package com.github.wesley.listeners;

import com.github.wesley.helper.RegisterListener;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ButtonClickListener implements org.javacord.api.listener.interaction.ButtonClickListener, RegisterListener {
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();

        if (buttonInteraction.getCustomId().equals("giveRole")) {
            buttonInteraction
                    .getServer()
                    .ifPresent(server -> {
                        List<Role> roleList = server.getRolesByName(SlashCommandListener.WATCHER_ROLE_NAME);

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
                    });
        }

        if (buttonInteraction.getCustomId().equals("removeRole")) {
            buttonInteraction
                    .getServer()
                    .ifPresent(server -> {
                        List<Role> roleList = server.getRolesByName(SlashCommandListener.WATCHER_ROLE_NAME);

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
                    });
        }
    }
}
