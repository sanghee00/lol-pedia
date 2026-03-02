package com.example.lolpedia.matches.dto;

import java.time.LocalDateTime;

public interface MatchSummaryProjection {
    Long getMatchId();

    String getMatchCode();

    LocalDateTime getMatchDate();

    String getTournament();

    String getTeamAName();

    String getTeamBName();

    Integer getTeamAScore();

    Integer getTeamBScore();

    String getTeamALogoUrl();

    String getTeamBLogoUrl();
}
