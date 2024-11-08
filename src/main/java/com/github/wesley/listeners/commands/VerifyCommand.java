package com.github.wesley.listeners.commands;

import com.github.wesley.models.Command;
import com.github.wesley.models.VerifyUser;
import com.github.wesley.models.tournament.Tournament;
import com.github.wesley.repositories.TournamentRepository;
import com.github.wesley.repositories.VerifyUserRepository;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class VerifyCommand extends Command {
    public static final String PLAYER_ROLE = "Player";
    //    private final String WYBIN_VERIFY_URL = "https://wybin.xyz/tournament-verify/";
    private final String WYBIN_VERIFY_URL = "http://localhost:4200/tournament-verify/";

    private final TournamentRepository tournamentRepository;
    private final VerifyUserRepository verifyUserRepository;

    @Autowired
    public VerifyCommand(TournamentRepository tournamentRepository, VerifyUserRepository verifyUserRepository) {
        this.tournamentRepository = tournamentRepository;
        this.verifyUserRepository = verifyUserRepository;

        this.commandName = "verify";
    }

    @Override
    public void execute(SlashCommandInteraction interaction) {
        Server server = interaction.getServer().get();
        User user = interaction.getUser();

        Tournament findTournament = tournamentRepository.findByDiscordServerId(server.getIdAsString());

        if (findTournament == null) {
            interaction
                    .createImmediateResponder()
                    .setContent("Unable to find a tournament linked to this Discord server.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        String verifyCode = String.valueOf(UUID.randomUUID());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);

        VerifyUser verifyUser = new VerifyUser(findTournament.getSlug(), user.getIdAsString(), verifyCode, calendar.getTime());

        verifyUserRepository.save(verifyUser);

        interaction
                .createImmediateResponder()
                .setContent("In order to verify yourself for this tournament, you need to click on the link listed at the bottom of this message. " +
                        "If you are not logged in to wyBin, you will be asked to login.\n" +
                        "Once you click on the link, simply follow the instructions there and you will be verified in no time.\n\r" +
                        "Verification link: " + WYBIN_VERIFY_URL + verifyCode)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }
}
