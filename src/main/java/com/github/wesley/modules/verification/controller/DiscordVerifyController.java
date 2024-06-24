package com.github.wesley.modules.verification.controller;

import com.github.wesley.modules.verification.models.DiscordVerification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class DiscordVerifyController {
    private static final String PATH_POST_DISCORD_VERIFY = "/discord-verify";

    @RequestMapping(path = PATH_POST_DISCORD_VERIFY, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void discordVerify(@RequestBody DiscordVerification discordVerification) {
        System.out.println("discord-verify");
        System.out.println(discordVerification);
    }
}
