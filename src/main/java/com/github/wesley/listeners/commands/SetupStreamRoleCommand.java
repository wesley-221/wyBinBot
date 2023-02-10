package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SetupStreamRoleCommand extends Command {
    public final static String WATCHER_ROLE_NAME = "Watcher";

    public SetupStreamRoleCommand() {
        this.commandName = "setupstreamrole";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        Optional<Server> server = interaction
                .getServer();

        if (server.isPresent()) {
            List<Role> watcherRoleList = server
                    .get()
                    .getRolesByName(WATCHER_ROLE_NAME);

            if (watcherRoleList.size() == 0) {
                server
                        .get()
                        .createRoleBuilder()
                        .setName(WATCHER_ROLE_NAME)
                        .create()
                        .whenComplete((role, throwable) -> {
                            if (throwable != null) {
                                interaction
                                        .createImmediateResponder()
                                        .setContent("Bot is missing permissions to create the Watcher role. Make sure the Bot has Manage Roles permission.")
                                        .setFlags(MessageFlag.EPHEMERAL)
                                        .respond();
                            }
                        });
            }

            new MessageBuilder()
                    .setContent("Click to get or remove the Watcher role to get pinged whenever the livestream goes live.")
                    .addComponents(
                            ActionRow.of(
                                    Button.success("giveRole", "Give Watcher role"),
                                    Button.danger("removeRole", "Remove Watcher role")
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
                                .setContent("Successfully setup Watcher role!")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();
                    });
        }
    }
}
