package com.example.lolpedia.playerhistory.dto;

import java.util.List;

public record PlayerHistoryListResponse(
    Long playerId,
    List<PlayerHistoryResponse> histories
) {

    public static PlayerHistoryListResponse of(
        Long playerId,
        List<PlayerHistoryResponse> playerHistoryResponse
    ) {
        return new PlayerHistoryListResponse(playerId, playerHistoryResponse);
    }
}
