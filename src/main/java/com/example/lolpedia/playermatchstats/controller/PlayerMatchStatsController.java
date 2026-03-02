package com.example.lolpedia.playermatchstats.controller;

import com.example.lolpedia.global.success.SuccessResponse;
import com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse;
import com.example.lolpedia.playermatchstats.enums.PlayerMatchStatsErrorCode;
import com.example.lolpedia.playermatchstats.enums.PlayerMatchStatsSuccessCode;
import com.example.lolpedia.playermatchstats.exception.PlayerMatchStatsException;
import com.example.lolpedia.playermatchstats.service.PlayerMatchStatsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/playermatchstatus")
@AllArgsConstructor
public class PlayerMatchStatsController {

    private final PlayerMatchStatsService playerMatchStatsService;

    /**
     * 개인 플레이어 매치 상세기록 조회 (플레이어 고유 아이디로 조회)
     *
     * @param playerId 플레이어 고유 아이디
     * @return ResponseEntity<SuccessResponse<List<PlayerMatchStatsResponse>>>
     * @throws PlayerMatchStatsException 조회 실패시 반환
     *
     */
    @GetMapping(params = "playerId")
    public ResponseEntity<SuccessResponse<List<PlayerMatchStatsResponse>>> getPlayerMatchStatisticsByPlayerId(
        @RequestParam Long playerId
    ) {
        List<PlayerMatchStatsResponse> playerMatchStatisticsResponses = playerMatchStatsService.findPlayerMatchStatisticsByPlayerId(playerId);

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerMatchStatsSuccessCode.PLAYER_MATCH_STATS_SUCCESS_FOUND, playerMatchStatisticsResponses)
        );
    }

    /**
     * 개인 플레이어 매치 상세기록 조회 (매치 고유 아이디로 조회)
     *
     * @param matchId 매치 고유 아이디
     * @return ResponseEntity<SuccessResponse<List<PlayerMatchStatsResponse>>>
     * @throws PlayerMatchStatsException 조회 실패시 반환
     *
     */
    @GetMapping(params = "matchId")
    public ResponseEntity<SuccessResponse<List<PlayerMatchStatsResponse>>> getPlayerMatchStatisticsByMatchId(
        @RequestParam Long matchId
    ) {
        List<PlayerMatchStatsResponse> playerMatchStatisticsResponses = playerMatchStatsService.findPlayerMatchStatisticsByMatchId(matchId);

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerMatchStatsSuccessCode.PLAYER_MATCH_STATS_SUCCESS_FOUND, playerMatchStatisticsResponses)
        );
    }


}
