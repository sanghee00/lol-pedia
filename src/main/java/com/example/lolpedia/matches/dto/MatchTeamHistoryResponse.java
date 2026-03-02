package com.example.lolpedia.matches.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 팀 상세 페이지 - 매치 히스토리 전용 경량 DTO
 * 프론트엔드에서 실제로 렌더링하는 13개 필드만 포함 (기존 MatchResponse 28개 필드 → 13개)
 *
 * 제거된 필드 (프론트엔드 미사용):
 * - gameName, patch, gameLength (게임 상세 정보)
 * - teamABans, teamBBans (밴 정보)
 * - teamADragons, teamBDragons, teamABarons, teamBBarons (오브젝트)
 * - teamATowers, teamBTowers (포탑)
 * - teamAGold, teamBGold, teamAKills, teamBKills (골드/킬)
 */
public record MatchTeamHistoryResponse(

        Long matchId,
        @JsonProperty("winner_team_id") Long winnerTeamId,
        @JsonProperty("team_a_id") Long teamAId,
        @JsonProperty("team_b_id") Long teamBId,

        String tournament,
        String matchCode,
        LocalDate matchDate,

        @JsonProperty("team_a_name") String teamAName,
        @JsonProperty("team_b_name") String teamBName,

        @JsonProperty("team_a_score") Integer teamAScore,
        @JsonProperty("team_b_score") Integer teamBScore,

        @JsonProperty("team_a_logo_url") String teamALogoUrl,
        @JsonProperty("team_b_logo_url") String teamBLogoUrl) {

    // JPQL Projection용 생성자 (LocalDateTime → LocalDate 변환)
    public MatchTeamHistoryResponse(
            Long matchId, Long winnerTeamId, Long teamAId, Long teamBId,
            String tournament, String matchCode, LocalDateTime matchDate,
            String teamAName, String teamBName,
            Integer teamAScore, Integer teamBScore,
            String teamALogoUrl, String teamBLogoUrl
    ) {
        this(
                matchId, winnerTeamId, teamAId, teamBId,
                tournament, matchCode,
                matchDate != null ? matchDate.toLocalDate() : null,
                teamAName, teamBName,
                teamAScore, teamBScore,
                getTeamImageOrDefault(teamALogoUrl),
                getTeamImageOrDefault(teamBLogoUrl));
    }

    @Nullable
    private static String getTeamImageOrDefault(String teamLogoUrl) {
        if (teamLogoUrl == null || teamLogoUrl.isBlank()) {
            return null;
        }
        return "team_images/" + teamLogoUrl;
    }
}
