package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import org.javacord.api.entity.message.MessageFlag;
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
                .setDescription("# Verifying yourself\n" +
                        "Verifying yourself is quick and easy! Follow these steps to gain access to your tournament roles and channels on Discord.\n" +

                        "## Join the Tournament's Discord server\n" +
                        "Ensure you're in the Discord server for the tournament you want to verify for.\n" +

                        "## Access Any Text Channel\n" +
                        "Navigate to any text channel where you can type messages.\n" +
                        "**Note**: This can be any text channel you can type in.\n" +

                        "## Use the `/verify` command\n" +
                        "Type and send the command `/verify`. The Discord bot will respond with a message containing a link.\n" +

                        "## Follow the Verification Link\n" +
                        "- Click the link in the bot's message.\n" +
                        "- You'll be redirected to the wyBin website, where it will display your verification status.\n" +
                        "- Once you see the message \"**You can now close this page!**\", you can close the tab and return to Discord.\n" +

                        "## Confirmation in Discord\n" +
                        "If you're a player in the tournament, the bot will send you a message confirming your verification.\n\n" +

                        "You are now successfully verified! Welcome to the tournament!");
        interaction
                .createImmediateResponder()
                .setFlags(MessageFlag.EPHEMERAL)
                .addEmbed(embed)
                .respond();
    }
}
