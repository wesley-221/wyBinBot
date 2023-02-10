package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class HowToVerifyCommand extends Command {
    public HowToVerifyCommand() {
        this.commandName = "howtoverify";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("How to verify yourself")
                .setColor(Color.GREEN)
                .setDescription("Verifying yourself is pretty straight forward. First go to https://wybin.xyz/osu-dashboard/discord-verify and login if you aren't logged in already.\n\n" +
                        "Once you are logged in, a Discord secret will be shown, copy this Discord secret.\n" +
                        "Now run the command `/verify` in any channel where you are able to type (and press enter, so a small box with `secret` shows up).\n\n" +
                        "Next paste the Discord secret you copied earlier and press enter. Your username has now been changed to your osu! username!");
        interaction
                .createImmediateResponder()
                .addEmbed(embed)
                .respond();
    }
}
