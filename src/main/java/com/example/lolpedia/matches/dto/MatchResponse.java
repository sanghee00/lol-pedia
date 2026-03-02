package com.example.lolpedia.matches.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MatchResponse(

        Long matchId,
        @JsonProperty("winner_team_id") Long winnerTeamId,
        @JsonProperty("team_a_id") Long teamAId,
        @JsonProperty("team_b_id") Long teamBId,

        String tournament,
        String matchCode,
        String gameName,
        String patch,
        String gameLength,
        LocalDate matchDate,

        @JsonProperty("team_a_name") String teamAName,
        @JsonProperty("team_b_name") String teamBName,

        @JsonProperty("team_a_bans") String teamABans,
        @JsonProperty("team_b_bans") String teamBBans,

        @JsonProperty("team_a_dragons") Integer teamADragons,
        @JsonProperty("team_b_dragons") Integer teamBDragons,

        @JsonProperty("team_a_barons") Integer teamABarons,
        @JsonProperty("team_b_barons") Integer teamBBarons,
        @JsonProperty("team_a_towers") Integer teamATowers,
        @JsonProperty("team_b_towers") Integer teamBTowers,

        @JsonProperty("team_a_gold") Integer teamAGold,
        @JsonProperty("team_b_gold") Integer teamBGold,
        @JsonProperty("team_a_kills") Integer teamAKills,
        @JsonProperty("team_b_kills") Integer teamBKills,
        @JsonProperty("team_a_score") Integer teamAScore,
        @JsonProperty("team_b_score") Integer teamBScore,

        @JsonProperty("team_a_logo_url") String teamAlogoUrl,
        @JsonProperty("team_b_logo_url") String teamBlogoUrl) {

    // JPQL Projection용 생성자 (LocalDateTime → LocalDate 변환)
    public MatchResponse(
            Long matchId, Long winnerTeamId, Long teamAId, Long teamBId,
            String tournament, String matchCode, String gameName,
            String patch, String gameLength, LocalDateTime matchDate,
            String teamAName, String teamBName,
            String teamABans, String teamBBans,
            Integer teamADragons, Integer teamBDragons,
            Integer teamABarons, Integer teamBBarons,
            Integer teamATowers, Integer teamBTowers,
            Integer teamAGold, Integer teamBGold,
            Integer teamAKills, Integer teamBKills,
            Integer teamAScore, Integer teamBScore,
            String teamAlogoUrl, String teamBlogoUrl) {
        this(
                matchId, winnerTeamId, teamAId, teamBId,
                tournament, matchCode, gameName,
                patch, gameLength,
                matchDate != null ? matchDate.toLocalDate() : null,
                teamAName, teamBName,
                teamABans, teamBBans,
                teamADragons, teamBDragons,
                teamABarons, teamBBarons,
                teamATowers, teamBTowers,
                teamAGold, teamBGold,
                teamAKills, teamBKills,
                teamAScore, teamBScore,
                teamAlogoUrl != null ? "team_images/" + teamAlogoUrl : null,
                teamBlogoUrl != null ? "team_images/" + teamBlogoUrl : null);
    }
}
