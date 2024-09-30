package com.github.wesley.listeners.discordsetup;

import com.github.wesley.services.SetupWyBinDiscordService;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateListener implements SelectMenuChooseListener, ButtonClickListener {
    private final SetupWyBinDiscordService setupWyBinDiscordService;

    @Autowired
    public TemplateListener(SetupWyBinDiscordService setupWyBinDiscordService) {
        this.setupWyBinDiscordService = setupWyBinDiscordService;
    }

    @Override
    public void onButtonClick(ButtonClickEvent buttonClickEvent) {

    }

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent selectMenuChooseEvent) {

    }
}
