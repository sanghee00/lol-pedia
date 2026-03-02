package com.example.lolpedia.matches.dto;

import com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record MatchInfoResponse(
    List<MatchResponse> playerMatchStatsList,
    List<PlayerMatchStatsResponse> playerMatchStatsResponseList
) { }
