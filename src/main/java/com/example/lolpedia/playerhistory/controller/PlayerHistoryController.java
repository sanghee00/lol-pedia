package com.example.lolpedia.playerhistory.controller;

import com.example.lolpedia.global.success.SuccessResponse;
import com.example.lolpedia.playerhistory.dto.PlayerHistoryListResponse;
import com.example.lolpedia.playerhistory.enums.PlayerHistorySuccessCode;
import com.example.lolpedia.playerhistory.exception.PlayerHistoryException;
import com.example.lolpedia.playerhistory.service.PlayerHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playerhistory")
@AllArgsConstructor
public class PlayerHistoryController {

    private final PlayerHistoryService playerHistoryService;

    /**
     * 플레이어의 과거부터 현재까지의 계약 정보를 가져온다.
     *
     * @param playerId 플레이어 고유 아이디
     * @return ResponseEntity<SuccessResponse<PlayerHistoryListResponse>>
     * @throws PlayerHistoryException 플레이어 계약 조회 실패시
     */
    @GetMapping
    public ResponseEntity<SuccessResponse<PlayerHistoryListResponse>> getPlayerHistory(
        @RequestParam Long playerId
    ) {
        PlayerHistoryListResponse playerHistory = playerHistoryService.findPlayerHistory(playerId);

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerHistorySuccessCode.PLAYER_HISTORY_SUCCESS_FOUND, playerHistory)
        );
    }

}
