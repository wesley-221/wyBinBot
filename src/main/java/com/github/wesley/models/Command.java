package com.github.wesley.models;

import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class Command {
    protected String commandName;

    public Command() {
    }

    public String getCommandName() {
        return commandName;
    }

    public abstract void execute(SlashCommandInteraction interaction);
}
