package com.example.lolpedia.team.dto;

import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.lang.Nullable;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record TeamResponse(
    Long teamId,
    String teamName,
    String teamKey,
    String teamNameShort,
    League league,
    String region,
    Boolean isDisbanded,
    String teamLogoUrl
) {

    public static List<TeamResponse> from(List<Team> teams) {
        return teams.stream().map(TeamResponse::toDto).toList();
    }

    public static TeamResponse of(Team team) {
        return toDto(team);
    }

    private static TeamResponse toDto(Team team) {

        return new TeamResponse(
            team.getTeamId(),
            team.getTeamName(),
            team.getTeamKey(),
            team.getTeamNameShort(),
            team.getLeague(),
            team.getRegion(),
            team.getIsDisbanded(),
            getTeamImageOrDefault(team)
        );
    }


    // ** TeamResponseWithPlayers이랑 같이 씀
    @Nullable
    static String getTeamImageOrDefault(Team team) {
        String teamLogoUrl = team.getTeamLogoUrl();
        if (existsTeamImage(teamLogoUrl)) {
            return null;
        }

        return "team_images/" + teamLogoUrl;
    }

    private static boolean existsTeamImage(String teamLogoUrl) {
        return teamLogoUrl == null || teamLogoUrl.isBlank();
    }
}
