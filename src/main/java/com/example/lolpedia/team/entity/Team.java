package com.example.lolpedia.team.entity;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.team.enums.League;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties({"players"})
public class Team {

    @Id
    @Column(name = "team_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;

    @Column(name = "team_key", nullable = false)
    private String teamKey;

    @Column(name = "team_name_short", nullable = false)
    private String teamNameShort;

    @Column(name = "league")
    @Enumerated(EnumType.STRING)
    private League league;

    @Column(name = "region")
    private String region;

    @Column(name = "team_logo_url", nullable = false)
    private String teamLogoUrl;

    @Column(name = "is_disbanded", nullable = false)
    private Boolean isDisbanded;

    @Builder
    public Team(
        String teamName, String teamKey, String teamNameShort, League league, String region,
        String teamLogoUrl, Boolean isDisbanded
    ) {
        this.teamName = teamName;
        this.teamKey = teamKey;
        this.teamNameShort = teamNameShort;
        this.league = league;
        this.region = region;
        this.teamLogoUrl = teamLogoUrl;
        this.isDisbanded = isDisbanded;
    }
}
