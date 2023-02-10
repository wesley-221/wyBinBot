package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Optional;

@Component
public class TimestampCommand extends Command {
    public TimestampCommand() {
        this.commandName = "timestamp";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> time = interactionOption.getStringValue();

        LocalTime localTime = LocalTime.parse(time.get());

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, localTime.getHour());
        now.set(Calendar.MINUTE, localTime.getMinute());
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        StringBuilder finalString = new StringBuilder();

        finalString.append("**Timestamps for the upcoming 10 days for ")
                .append(localTime)
                .append(" UTC+0**\n\r");

        for (int i = 0; i < 10; i++) {
            finalString
                    .append("<t:")
                    .append(now.getTimeInMillis() / 1000L)
                    .append(">: ")
                    .append("`<t:")
                    .append(now.getTimeInMillis() / 1000L)
                    .append("> (")
                    .append("<t:")
                    .append(now.getTimeInMillis() / 1000L)
                    .append(":R>)`")
                    .append("\n");

            now.add(Calendar.DATE, 1);
        }

        interaction
                .createImmediateResponder()
                .setContent(finalString.toString())
                .respond();
    }
}
