package com.github.wesley.listeners;

import com.github.wesley.helper.Log;
import com.github.wesley.helper.RegisterListener;
import com.github.wesley.models.Command;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SlashCommandListener implements SlashCommandCreateListener, RegisterListener {
    private final List<Command> allCommands = new ArrayList<>();

    @Autowired
    public SlashCommandListener(ApplicationContext applicationContext) {
        for (Command command : applicationContext.getBeansOfType(Command.class).values()) {
            if (command.getCommandName() == null) {
                Log.error("Unable to register Slash Command " + command.getClass() + ". You have to set the command name in order for it to be recognized.");
                continue;
            }

            allCommands.add(command);
            Log.info("Registered the Slash Command " + command.getClass().getName());
        }
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String commandName = interaction.getCommandName();

        Command command = this.getCommandByName(commandName);

        if (command != null) {
            command.execute(interaction);
        }
    }

    /**
     * Get a command by the given name
     *
     * @param commandName the name of the command
     * @return the command
     */
    private Command getCommandByName(String commandName) {
        Command foundCommand = null;

        for (Command command : this.allCommands) {
            if (command.getCommandName().equals(commandName)) {
                foundCommand = command;
            }
        }

        return foundCommand;
    }
}
