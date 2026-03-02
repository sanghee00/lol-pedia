package com.example.lolpedia.player.controller;

import com.example.lolpedia.global.success.SuccessResponse;
import com.example.lolpedia.player.dto.PlayerInfoResponse;
import com.example.lolpedia.player.dto.PlayerResponse;
import com.example.lolpedia.player.enums.PlayerSuccessCode;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.player.service.PlayerService;
import com.example.lolpedia.team.enums.League;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    /**
     * 플레이어 이름으로 하나의 플레이어를 찾는다.
     *
     * @param name 검색할 플레이어 이름
     * @return PlayerResponse
     * @throws PlayerException 플레이어 조회 실패시
     */
    @GetMapping(value = "/player", params = "name")
    public ResponseEntity<SuccessResponse<PlayerResponse>> getPlayerByPlayerName(@RequestParam String name) {
        PlayerResponse playerResponse = playerService.findPlayerByPlayerName(name);

        return toSuccessResponse(playerResponse);
    }

    /**
     * 플레이어 아이디로 하나의 플레이어를 찾는다.
     *
     * @param id 검색할 플레이어 이름
     * @return PlayerResponse
     * @throws PlayerException 플레이어 조회 실패시
     */
    @GetMapping(value = "/player", params = "id")
    public ResponseEntity<SuccessResponse<PlayerResponse>> getPlayerByPlayerId(@RequestParam long id) {
        PlayerResponse playerResponse = playerService.findPlayerByPlayerId(id);

        return toSuccessResponse(playerResponse);
    }

    /**
     * 이름 순으로 8명의 플레이어를 찾는다.
     *
     * @return PlayerResponse
     * @throws PlayerException 플레이어 조회 실패시
     */
    @GetMapping(value = "/players")
    public ResponseEntity<SuccessResponse<List<PlayerResponse>>> getPlayers() {
        List<PlayerResponse> playerResponse = playerService.findEightPlayer();

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerSuccessCode.PLAYER_SUCCESS_FOUND, playerResponse)
        );
    }

    /**
     * 플레이어 페이징네이션 API 5개의 조건을 걸수 있으며 default(page, size)는 필수
     *
     * @param page 몇 번째 페이지인지
     * @param size 한 페이지당 몇개의 크기를 반환 받을 것인지
     * @param country 플레이어 나라
     * @param position 플레이어 포지션(TOP, JUG, MID, BOT, SUP)
     * @param league 지역 리그( LCK(대한민국), LCP(중국), LEC(EMEA), LCP(퍼시픽), LCS(북중미), CBLOL(남미) )
     * @param keyword 검색 단어 ( 플레이어, 팀 ) 검색
     *
     * @return ResponseEntity<SuccessResponse<Page<PlayerResponse>>>
     * @throws PlayerException 플레이어 조회 실패시
     */
    @GetMapping("/players/paged")
    public ResponseEntity<SuccessResponse<Page<PlayerResponse>>> getPlayersWithPaging(
        @RequestParam @Min(0) int page,
        @RequestParam @Min(1) @Max(100) int size,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) Position position,
        @RequestParam(required = false) League league,
        @RequestParam(required = false) String keyword
    ) {

        Page<PlayerResponse> playerResponsePage =
            playerService.findPlayerWithPaging(
                page,
                size,
                country,
                position,
                league,
                keyword
            );

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerSuccessCode.PLAYER_SUCCESS_FOUND, playerResponsePage));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<SuccessResponse<PlayerInfoResponse>> getPlayerInfo(
        @PathVariable Long playerId
    ) {
        PlayerInfoResponse playerInfoResponse = playerService.findPlayerInfo(playerId);

        return ResponseEntity.ok(
            SuccessResponse.of(PlayerSuccessCode.PLAYER_INFO_SUCCESS_FOUND, playerInfoResponse)
        );
    }

    private ResponseEntity<SuccessResponse<PlayerResponse>> toSuccessResponse(PlayerResponse playerResponse) {
        return ResponseEntity.ok(
            SuccessResponse.of(PlayerSuccessCode.PLAYER_SUCCESS_FOUND, playerResponse)
        );
    }

}
