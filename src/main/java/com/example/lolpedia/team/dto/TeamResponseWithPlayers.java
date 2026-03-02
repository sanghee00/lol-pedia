package com.example.lolpedia.team.dto;

import com.example.lolpedia.player.dto.PlayerResponse;
import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

import static com.example.lolpedia.team.dto.TeamResponse.getTeamImageOrDefault;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record TeamResponseWithPlayers(
    Long teamId,
    String teamName,
    String teamKey,
    String teamNameShort,
    League league,
    String region,
    Boolean isDisbanded,
    String teamLogoUrl,
    List<PlayerResponse> players
) {

    // ** 플레이어 포함 Team 반환
    public static TeamResponseWithPlayers of(Team team) {
        List<PlayerResponse> playerResponse = getPlayerResponses(team);

        return toDtoWithPlayers(team, playerResponse);
    }

    private static List<PlayerResponse> getPlayerResponses(Team team) {
        List<Player> players = team.getPlayers();
        return players.stream()
            .map(PlayerResponse::of)
            .toList();
    }

    private static TeamResponseWithPlayers toDtoWithPlayers(Team team, List<PlayerResponse> playerResponse) {

        return new TeamResponseWithPlayers(
            team.getTeamId(),
            team.getTeamName(),
            team.getTeamKey(),
            team.getTeamNameShort(),
            team.getLeague(),
            team.getRegion(),
            team.getIsDisbanded(),
            getTeamImageOrDefault(team),
            playerResponse
        );
    }

}
