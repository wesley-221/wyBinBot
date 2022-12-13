package com.github.wesley.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    private Long id;
    private String slug;
    private String username;
    private String discordSecret;
}
