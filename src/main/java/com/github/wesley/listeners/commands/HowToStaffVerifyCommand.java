package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;

@Component
public class HowToStaffVerifyCommand extends Command {
    public HowToStaffVerifyCommand() {
        this.commandName = "howtostaffverify";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        SlashCommandInteractionOption interactionOption = interaction.getArguments().get(0);
        Optional<String> url = interactionOption.getStringValue();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("How to verify yourself as a staff member")
                .setColor(Color.GREEN)
                .setDescription("Verifying yourself is pretty straight forward. First go to " + url.get() + " and login if you aren't logged in already.\n\n" +
                        "Once you are logged in, a box with `Discord verification` will show up on top of the page. Click on it to show the Discord secret, after that copy this Discord secret.\n" +
                        "Now run the command `/staffverify` in any channel where you are able to type (and press enter, so a small box with `secret` shows up).\n\n" +
                        "Next paste the Discord secret you copied earlier and press enter. Your username has now been changed to your osu! username!");
        interaction
                .createImmediateResponder()
                .addEmbed(embed)
                .respond();
    }
}
