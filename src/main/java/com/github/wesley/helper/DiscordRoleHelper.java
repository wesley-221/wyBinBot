package com.github.wesley.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javacord.api.entity.message.component.ActionRow;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscordRoleHelper {
    String description;
    ActionRow actionrow;
}
