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
                .setColor(Color.GREEN)
                .setDescription("## Verify yourself\n" +
                        "Verifying yourself is only used for when you are not a participant or a staff member. If you are either one of those, follow the steps linked below.\n" +
                        "First go to https://wybin.xyz/osu-dashboard/discord-verify and login if you aren't logged in already.\n" +
                        "Once you are logged in, a Discord secret will be shown, copy this Discord secret.\n" +
                        "Now run the command `/verify` in any channel where you are able to type (and press enter, so a small box with `secret` shows up).\n\n" +
                        "Next paste the Discord secret you copied earlier and press enter. Your username has now been changed to your osu! username!\n\n" +

                        "## Verify as a player\n" +
                        "Go to the tournament you want to verify yourself for, scroll down and go to the Discord tab. Copy the secret that starts with `player-username-xxxxxxxx`.\n" +
                        "Now run the command `/verify` in any channel where you are able to type (and press enter, so a small box with `secret` shows up).\n" +
                        "Next paste the Discord secret you copied earlier and press enter. Your username has now been changed to your osu! username!\n\n" +

                        "## Verify as a staff member\n" +
                        "Go to the tournament you want to verify yourself for and go to the staff page. Now open the Discord verification box and copy the secret that starts with `staff-username-********`.\n" +
                        "Now run the command `/verify` in any channel where you are able to type (and press enter, so a small box with `secret` shows up).\n" +
                        "Next paste the Discord secret you copied earlier and press enter. Your username has now been changed to your osu! username!");
        interaction
                .createImmediateResponder()
                .addEmbed(embed)
                .respond();
    }
}
