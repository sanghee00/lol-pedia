package com.example.lolpedia.player.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record PlayerInfoResponse(
    PlayerSeasonStatsResponse playerProfile,
    List<PlayerSeasonRecentMatchResponse> recentMatches
) { }
