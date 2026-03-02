package com.example.lolpedia.playermatchstats.entity;

import com.example.lolpedia.matches.entity.Matches;
import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.playermatchstats.enums.Side;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "player_match_stats", indexes = {
    @Index(name = "idx_player_match_stats_match_code", columnList = "match_code"),
    @Index(name = "idx_player_match_stats_player_id", columnList = "player_Id")
})
public class PlayerMatchStats {

    @Id
    @Column(name = "stat_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_Id")
    private Player playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Matches matchId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "champion_played", nullable = false)
    private String championPlayed;

    @Column(name = "kills", nullable = false)
    private Integer kills;

    @Column(name = "deaths", nullable = false)
    private Integer deaths;

    @Column(name = "assists", nullable = false)
    private Integer assists;

    @Column(name = "damage", nullable = false)
    private Integer damage;

    @Column(name = "gold", nullable = false)
    private Integer gold;

    @Column(name = "cs", nullable = false)
    private Integer cs;

    @Column(name = "vision_score")
    private Integer visionScore;

    @Column(name = "summoner_spells")
    private String summonerSpells;

    @Column(name = "trinket")
    private String trinket;

    @Column(name = "side")
    @Enumerated(EnumType.STRING)
    private Side side;

    @Column(name = "match_code", nullable = false)
    private String matchCode;

    @Column(name = "game_code", nullable = false)
    private String gameCode;

    @Column(name = "runes", nullable = false)
    private String runes;

    @Builder
    public PlayerMatchStats(
        Player playerId, Matches matchId, String playerName, String championPlayed, Integer kills, Integer deaths,
        Integer assists, Integer damage, Integer gold, Integer cs, Integer visionScore, String summonerSpells,
        String trinket, Side side, String matchCode, String gameCode, String runes
    ) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.playerName = playerName;
        this.championPlayed = championPlayed;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.damage = damage;
        this.gold = gold;
        this.cs = cs;
        this.visionScore = visionScore;
        this.summonerSpells = summonerSpells;
        this.trinket = trinket;
        this.side = side;
        this.matchCode = matchCode;
        this.gameCode = gameCode;
        this.runes = runes;
    }
}
