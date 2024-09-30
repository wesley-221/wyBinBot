package com.github.wesley.helper;

import com.github.wesley.models.tournament.Tournament;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.SelectMenuInteraction;

import java.awt.*;

public class SetupDiscordHelper {
    public static Boolean doesDropDownWork(SelectMenuChooseEvent event, Tournament tournament) {
        SelectMenuInteraction selectMenuInteraction = event.getSelectMenuInteraction();

        if (tournament == null) {
            selectMenuInteraction
                    .createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setDescription("This dropdown no longer works, please re-run the setup command.")
                            .setColor(Color.RED))
                    .respond();
            return false;
        }

        return true;
    }
}
