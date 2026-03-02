package com.example.lolpedia.matches.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record MatchSummaryResponse(
    Long matchId,
    String matchCode,
    LocalDateTime matchDate,
    String tournament,
    String teamAName,
    String teamBName,
    Integer teamAScore,
    Integer teamBScore,
    String teamALogoUrl,
    String teamBLogoUrl
) {
    public static MatchSummaryResponse from(MatchSummaryProjection projection) {
        return new MatchSummaryResponse(
            projection.getMatchId(),
            projection.getMatchCode(),
            projection.getMatchDate(),
            projection.getTournament(),
            projection.getTeamAName(),
            projection.getTeamBName(),
            projection.getTeamAScore(),
            projection.getTeamBScore(),
            projection.getTeamALogoUrl(),
            projection.getTeamBLogoUrl()
        );
    }
}
