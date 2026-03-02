package com.example.lolpedia.player.dto;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.Position;
import org.springframework.lang.Nullable;

public record PlayerSeasonStatsResponse(
    String playerName,
    String playerNativeName,
    Position mainPosition,
    String playerCountry,
    String playerImageUrl,
    String teamName,
    String startContractYear,
    String endContractYear,

    // 계산된 결과 필드 (실제 Record가 저장하는 값)
    Double kda,
    Double csPerMinute,
    Double killParticipation,
    Double winRate,
    Long totalGames
) {
    public PlayerSeasonStatsResponse(
        String playerName,
        String playerNativeName,
        Position mainPosition,
        String playerCountry,
        String playerImageUrl,
        String teamName,
        String startContractYear,
        String endContractYear,
        Long totalKills,
        Long totalDeaths,
        Long totalAssists,
        Long totalCs,
        Long totalGameLength,
        Long totalTeamKills,
        Long totalWins,
        Long count
    ) {
        this(
            playerName,
            playerNativeName,
            mainPosition,
            playerCountry,
            getPlayerImageOrDefault(playerImageUrl),
            teamName,
            startContractYear,
            endContractYear,
            calculateKda(totalKills, totalDeaths, totalAssists),
            calculateCsPerMinute(totalCs, totalGameLength),
            calculateKillParticipation(totalKills, totalAssists, totalTeamKills),
            calculateWinRate(totalWins, count),
            (count != null) ? count : 0L
        );
    }

    private static double calculateKda(Long kills, Long deaths, Long assists) {
        long k = (kills == null) ? 0 : kills;
        long d = (deaths == null || deaths == 0) ? 1 : deaths; // 데스가 0이면 1로 처리
        long a = (assists == null) ? 0 : assists;

        return Math.round(((double)(k + a) / d) * 100) / 100.0;
    }

    private static double calculateCsPerMinute(Long cs, Long gameLength) {
        if (gameLength == null || gameLength == 0) return 0.0;
        long totalCs = (cs == null) ? 0 : cs;

        double totalMinutes = gameLength / 60.0;
        if (totalMinutes == 0) return 0.0;

        return Math.round((totalCs / totalMinutes) * 100) / 100.0;
    }

    private static double calculateKillParticipation(Long kills, Long assists, Long teamKills) {
        if (teamKills == null || teamKills == 0) return 0.0;
        long k = (kills == null) ? 0 : kills;
        long a = (assists == null) ? 0 : assists;

        return Math.round(((double)(k + a) / teamKills * 100) * 10) / 10.0;
    }

    private static double calculateWinRate(Long wins, Long count) {
        if (count == null || count == 0) return 0.0;
        long w = (wins == null) ? 0 : wins;

        return Math.round(((double) w / count * 100) * 100) / 100.0;
    }

    @Nullable
    private static String getPlayerImageOrDefault(String playerImageUrl) {
        if (playerImageUrl.isBlank()) {
            return null;
        }

        return "player_images/" + playerImageUrl;
    }
}
