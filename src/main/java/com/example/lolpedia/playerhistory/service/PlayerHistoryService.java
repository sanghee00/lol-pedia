package com.example.lolpedia.playerhistory.service;

import com.example.lolpedia.playerhistory.dto.PlayerHistoryListResponse;
import com.example.lolpedia.playerhistory.dto.PlayerHistoryResponse;
import com.example.lolpedia.playerhistory.entity.PlayerHistory;
import com.example.lolpedia.playerhistory.enums.PlayerHistoryErrorCode;
import com.example.lolpedia.playerhistory.exception.PlayerHistoryException;
import com.example.lolpedia.playerhistory.repository.PlayerHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlayerHistoryService {

    private final PlayerHistoryRepository playerHistoryRepository;

    public PlayerHistoryListResponse findPlayerHistory(Long playerId) {
        List<PlayerHistory> playerHistory = playerHistoryRepository.findByPlayer_PlayerId(playerId);
        if (playerHistory.isEmpty()) {
            throw new PlayerHistoryException(PlayerHistoryErrorCode.PLAYER_HISTORY_NOT_FOUND);
        }

        List<PlayerHistoryResponse> playerHistoryResponse = PlayerHistoryResponse.from(playerHistory);

        return PlayerHistoryListResponse.of(playerId, playerHistoryResponse);
    }

}
