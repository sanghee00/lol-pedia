package com.example.lolpedia.matches.entity;

import com.example.lolpedia.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matches", indexes = {
    @Index(name = "idx_match_date_code", columnList = "match_date, match_code"),
    @Index(name = "idx_match_match_code_game_code", columnList = "match_code, game_code"),
    @Index(name = "idx_match_match_code_match_date", columnList = "match_code, match_date DESC")
})
public class Matches {

    @Id
    @Column(name = "match_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @Column(name = "winner_team_name")
    private String winnerTeamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loss_team_id")
    private Team lossTeam;

    @Column(name = "loss_team_name")
    private String lossTeamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_a_id")
    private Team teamAId;

    @Column(name = "team_a_name")
    private String teamAName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_b_id")
    private Team teamBId;

    @Column(name = "team_b_name")
    private String teamBName;

    // 매치 메타 정보
    @Column(name = "tournament")
    private String tournament;

    @Column(name = "event_slug")
    private String eventSlug;

    @Column(name = "match_code")
    private String matchCode;

    @Column(name = "game_code")
    private String gameCode;

    @Column(name = "match_name")
    private String matchName;

    @Column(name = "game_name")
    private String gameName; // 몇 세트인지

    @Column(name = "patch")
    private String patch; // 패치 버전

    @Column(name = "game_length")
    private String gameLength;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    // 밴 정보
    @Column(name = "team_a_bans", nullable = false)
    private String teamABans;

    @Column(name = "team_b_bans", nullable = false)
    private String teamBBans;

    // 픽
    @Column(name = "team_a_picks", nullable = false)
    private String teamAPicks;

    @Column(name = "team_b_picks", nullable = false)
    private String teamBPicks;

    // 플레이어
    @Column(name = "team_a_players", nullable = false)
    private String teamAPlayers;

    @Column(name = "team_b_players", nullable = false)
    private String teamBPlayers;

    // 꼬물이 (Void Grubs)
    @Column(name = "team_a_void_grubs")
    private Integer teamAVoidGrubs;

    @Column(name = "team_b_void_grubs")
    private Integer teamBVoidGrubs;

    // 정령 (Rift Heralds)
    @Column(name = "team_a_rift_heralds")
    private Integer teamARiftHeralds;

    @Column(name = "team_b_rift_heralds")
    private Integer teamBRiftHeralds;

    // 드래곤 총합
    @Column(name = "team_a_dragons")
    private Integer teamADragons;

    @Column(name = "team_b_dragons")
    private Integer teamBDragons;

    // 속성 드래곤
    @Column(name = "team_a_clouds")
    private Integer teamAClouds;

    @Column(name = "team_b_clouds")
    private Integer teamBClouds;

    @Column(name = "team_a_infernals")
    private Integer teamAInfernals;

    @Column(name = "team_b_infernals")
    private Integer teamBInfernals;

    @Column(name = "team_a_mountains")
    private Integer teamAMountains;

    @Column(name = "team_b_mountains")
    private Integer teamBMountains;

    @Column(name = "team_a_oceans")
    private Integer teamAOceans;

    @Column(name = "team_b_oceans")
    private Integer teamBOceans;

    @Column(name = "team_a_hextechs")
    private Integer teamAHextechs;

    @Column(name = "team_b_hextechs")
    private Integer teamBHextechs;

    @Column(name = "team_a_chemtechs")
    private Integer teamAChemtechs;

    @Column(name = "team_b_chemtechs")
    private Integer teamBChemtechs;

    @Column(name = "team_a_elders")
    private Integer teamAElders;

    @Column(name = "team_b_elders")
    private Integer teamBElders;

    // 바론
    @Column(name = "team_a_barons")
    private Integer teamABarons;

    @Column(name = "team_b_barons")
    private Integer teamBBarons;

    // 포탑
    @Column(name = "team_a_towers")
    private Integer teamATowers;

    @Column(name = "team_b_towers")
    private Integer teamBTowers;

    // 억제기
    @Column(name = "team_a_inhibitors")
    private Integer teamAInhibitors;

    @Column(name = "team_b_inhibitors")
    private Integer teamBInhibitors;

    // 골드
    @Column(name = "team_a_gold")
    private Integer teamAGold;

    @Column(name = "team_b_gold")
    private Integer teamBGold;

    // 킬
    @Column(name = "team_a_kills")
    private Integer teamAKills;

    @Column(name = "team_b_kills")
    private Integer teamBKills;

    // 아타칸
    @Column(name = "team_a_atakhans")
    private Integer teamAAtakhans;

    @Column(name = "team_b_atakhans")
    private Integer teamBAtakhans;

    // 최종 스코어
    @Column(name = "team_a_score")
    private Integer teamAScore;

    @Column(name = "team_b_score")
    private Integer teamBScore;

    @Builder
    public Matches(
        Team winnerTeam, String winnerTeamName, Team lossTeam, String lossTeamName, Team teamAId, String teamAName,
        Team teamBId, String teamBName, String tournament, String eventSlug, String matchCode, String gameCode,
        String matchName, String gameName, String patch, String gameLength, LocalDateTime matchDate, String teamABans,
        String teamBBans, String teamAPicks, String teamBPicks, String teamAPlayers, String teamBPlayers,
        Integer teamAVoidGrubs, Integer teamBVoidGrubs, Integer teamARiftHeralds, Integer teamBRiftHeralds,
        Integer teamADragons, Integer teamBDragons, Integer teamAClouds, Integer teamBClouds, Integer teamAInfernals,
        Integer teamBInfernals, Integer teamAMountains, Integer teamBMountains, Integer teamAOceans,
        Integer teamBOceans,
        Integer teamAHextechs, Integer teamBHextechs, Integer teamAChemtechs, Integer teamBChemtechs,
        Integer teamAElders,
        Integer teamBElders, Integer teamABarons, Integer teamBBarons, Integer teamATowers, Integer teamBTowers,
        Integer teamAInhibitors, Integer teamBInhibitors, Integer teamAGold, Integer teamBGold, Integer teamAKills,
        Integer teamBKills, Integer teamAAtakhans, Integer teamBAtakhans, Integer teamAScore, Integer teamBScore
    ) {
        this.winnerTeam = winnerTeam;
        this.winnerTeamName = winnerTeamName;
        this.lossTeam = lossTeam;
        this.lossTeamName = lossTeamName;
        this.teamAId = teamAId;
        this.teamAName = teamAName;
        this.teamBId = teamBId;
        this.teamBName = teamBName;
        this.tournament = tournament;
        this.eventSlug = eventSlug;
        this.matchCode = matchCode;
        this.gameCode = gameCode;
        this.matchName = matchName;
        this.gameName = gameName;
        this.patch = patch;
        this.gameLength = gameLength;
        this.matchDate = matchDate;
        this.teamABans = teamABans;
        this.teamBBans = teamBBans;
        this.teamAPicks = teamAPicks;
        this.teamBPicks = teamBPicks;
        this.teamAPlayers = teamAPlayers;
        this.teamBPlayers = teamBPlayers;
        this.teamAVoidGrubs = teamAVoidGrubs;
        this.teamBVoidGrubs = teamBVoidGrubs;
        this.teamARiftHeralds = teamARiftHeralds;
        this.teamBRiftHeralds = teamBRiftHeralds;
        this.teamADragons = teamADragons;
        this.teamBDragons = teamBDragons;
        this.teamAClouds = teamAClouds;
        this.teamBClouds = teamBClouds;
        this.teamAInfernals = teamAInfernals;
        this.teamBInfernals = teamBInfernals;
        this.teamAMountains = teamAMountains;
        this.teamBMountains = teamBMountains;
        this.teamAOceans = teamAOceans;
        this.teamBOceans = teamBOceans;
        this.teamAHextechs = teamAHextechs;
        this.teamBHextechs = teamBHextechs;
        this.teamAChemtechs = teamAChemtechs;
        this.teamBChemtechs = teamBChemtechs;
        this.teamAElders = teamAElders;
        this.teamBElders = teamBElders;
        this.teamABarons = teamABarons;
        this.teamBBarons = teamBBarons;
        this.teamATowers = teamATowers;
        this.teamBTowers = teamBTowers;
        this.teamAInhibitors = teamAInhibitors;
        this.teamBInhibitors = teamBInhibitors;
        this.teamAGold = teamAGold;
        this.teamBGold = teamBGold;
        this.teamAKills = teamAKills;
        this.teamBKills = teamBKills;
        this.teamAAtakhans = teamAAtakhans;
        this.teamBAtakhans = teamBAtakhans;
        this.teamAScore = teamAScore;
        this.teamBScore = teamBScore;
    }
}
