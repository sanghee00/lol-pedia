package com.example.lolpedia.playerhistory.dto;

import com.example.lolpedia.playerhistory.entity.PlayerHistory;

import java.time.LocalDate;
import java.util.List;

public record PlayerHistoryResponse(
    Long playerHistoryId,
    Long teamId,
    String startContractDate,
    String endContractDate
) {

    public static List<PlayerHistoryResponse> from(List<PlayerHistory> playerHistories) {
        return playerHistories.stream()
            .map(history -> new PlayerHistoryResponse(
                history.getPlayerHistoryId(),
                history.getTeam().getTeamId(),
                history.getStartContractYear(),
                history.getEndContractYear()
            )).toList();
    }

}
