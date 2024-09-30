package com.github.wesley.services;

import com.github.wesley.models.tournament.Tournament;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SetupWyBinDiscordService {
    public final static String WYBIN_SETUP_WEBHOOKS_BUTTON = "wybin-setup-webhooks";
    public final static String WYBIN_SETUP_ROLES_BUTTON = "wybin-setup-roles";
    public final static String WYBIN_RESCHEDULE_EVERYONE = "wybin-reschedule-everone";
    public final static String WYBIN_RESCHEDULE_SCHEDULER = "wybin-reschedule-scheduler";
    public final static String WYBIN_STAFF_REGISTRATION = "wybin-staff-registration";
    public final static String WYBIN_MATCH_PING_STAFF = "wybin-match-ping-staff";
    public final static String WYBIN_MATCH_PING_PARTICIPANTS = "wybin-match-ping-participants";

    private final Map<String, Tournament> buttonForTournament = new HashMap<>();
    private final Map<String, Tournament> dropdownForTournament = new HashMap<>();

    public void addButtonForTournament(String customId, Tournament tournament) {
        buttonForTournament.put(customId, tournament);
    }

    public void addButtonsForTournament(List<String> customIds, Tournament tournament) {
        for (String customId : customIds) {
            buttonForTournament.put(customId, tournament);
        }
    }

    public Tournament getButtonForTournament(String customId) {
        return buttonForTournament.get(customId);
    }

    public void addDropdownForTournament(String customId, Tournament tournament) {
        dropdownForTournament.put(customId, tournament);
    }

    public Tournament getDropdownForTournament(String customId) {
        return dropdownForTournament.get(customId);
    }

    public String createCustomId(String prefix) {
        return prefix + UUID.randomUUID();
    }

    public Boolean isSetupRolesButton(String customId) {
        return customId.contains(WYBIN_SETUP_ROLES_BUTTON);
    }

    public Boolean isSetupWebhooksButton(String customId) {
        return customId.contains(WYBIN_SETUP_WEBHOOKS_BUTTON);
    }

    public Boolean isSetupRescheduleForEveryone(String customId) {
        return customId.contains(WYBIN_RESCHEDULE_EVERYONE);
    }
}
