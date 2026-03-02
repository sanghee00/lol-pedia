package com.example.lolpedia.player.entity;

import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @Column(name = "player_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "player_key", nullable = false, unique = true)
    private String playerKey;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "player_native_name", nullable = false)
    private String playerNativeName;

    @Column(name = "player_name_full", nullable = false)
    private String playerNameFull;

    @Column(name = "player_country", nullable = false)
    private String playerCountry;

    // 역할(라인)
    @Column(name = "main_position", nullable = false)
    @Enumerated(EnumType.STRING)
    private Position mainPosition;

    @Column(name = "player_image_url")
    private String playerImageUrl;

    @Builder
    public Player(
        Team team, String playerKey, String playerName, String playerNativeName, String playerNameFull,
        String playerCountry, Position mainPosition, String playerImageUrl
    ) {
        this.team = team;
        this.playerKey = playerKey;
        this.playerName = playerName;
        this.playerNativeName = playerNativeName;
        this.playerNameFull = playerNameFull;
        this.playerCountry = playerCountry;
        this.mainPosition = mainPosition;
        this.playerImageUrl = playerImageUrl;
    }
}
