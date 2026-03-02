package com.example.lolpedia.player.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PlayerSeasonRecentMatchResponse(
    String matchResult,   // "WIN" or "LOSE"
    String score,         // "2 : 1"
    String matchDateStr,  // "1월 25일"
    String tournamentName,
    @JsonProperty("team_a_name") String teamAName,
    @JsonProperty("team_b_name") String teamBName,
    String duration,      // "34:21"
    String kdaStr,        // "5/2/10"
    String championPlayed,
    Long cs
) {

    public PlayerSeasonRecentMatchResponse(
        Long winnerTeamId,
        Long playerTeamId, // 승패 판별용 ID들
        Integer teamAScore,
        Integer teamBScore,
        LocalDateTime matchDate,
        String tournamentName,
        String teamAName,
        String teamBName,
        Long gameLength, // 초 단위
        Long kills,
        Long deaths,
        Long assists,
        String championPlayed,
        Long cs
    ) {
        this(
            (winnerTeamId != null && winnerTeamId.equals(playerTeamId)) ? "WIN" : "LOSE",
            teamAScore + " : " + teamBScore,
            (matchDate != null) ? matchDate.format(DateTimeFormatter.ofPattern("M월 d일")) : "",
            tournamentName,
            teamAName,
            teamBName,
            formatDuration(gameLength),
            kills + "/" + deaths + "/" + assists,
            championPlayed,
            cs
        );
    }

    private static final int SECONDS_PER_MINUTE = 60;

    // 시간 변환 헬퍼 메서드
    private static String formatDuration(Long gameLength) {
        if (gameLength == null) return "0:00";
        long minutes = gameLength / SECONDS_PER_MINUTE;
        long seconds = gameLength % SECONDS_PER_MINUTE;
        return String.format("%d:%02d", minutes, seconds);
    }
}
