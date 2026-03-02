package com.example.lolpedia.team.controller;

import com.example.lolpedia.global.success.SuccessResponse;
import com.example.lolpedia.player.dto.PlayerResponse;
import com.example.lolpedia.player.enums.PlayerSuccessCode;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.team.dto.TeamResponse;
import com.example.lolpedia.team.dto.TeamResponseWithPlayers;
import com.example.lolpedia.team.enums.League;
import com.example.lolpedia.team.enums.TeamSuccessCode;
import com.example.lolpedia.team.exception.TeamException;
import com.example.lolpedia.team.service.TeamService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     *  팀명으로 팀에 대한 상세 내용 조회
     *
     * @param teamName 팀명
     * @return ResponseEntity<SuccessResponse<TeamResponseWithPlayers>>
     * @throws TeamException 팀 조회 실패시 반환
     */
    @GetMapping("/team-name")
    public ResponseEntity<SuccessResponse<TeamResponseWithPlayers>> getTeamByName(@RequestParam String teamName) {
        TeamResponseWithPlayers teamResponseWithPlayers = teamService.findByTeamNameWithPlayers(teamName);

        return ResponseEntity.ok(
            SuccessResponse.of(TeamSuccessCode.TEAM_SUCCESS_FOUND, teamResponseWithPlayers)
        );
    }

    /**
     *  팀 고유 아이디로 팀에 대한 상세 내용 조회
     *
     * @param teamId 팀명
     * @return ResponseEntity<SuccessResponse<TeamResponseWithPlayers>>
     * @throws TeamException 팀 조회 실패시 반환
     */
    @GetMapping("/team-id")
    public ResponseEntity<SuccessResponse<TeamResponseWithPlayers>> getTeamById(@RequestParam Long teamId) {
        TeamResponseWithPlayers teamResponseWithPlayers = teamService.findByTeamIdWithPlayers(teamId);

        return ResponseEntity.ok(
            SuccessResponse.of(TeamSuccessCode.TEAM_SUCCESS_FOUND, teamResponseWithPlayers)
        );
    }

    /**
     *  8개의 팀 상세 정보 조회
     *
     * @return ResponseEntity<SuccessResponse<TeamResponseWithPlayers>>
     * @throws TeamException 팀 조회 실패시 반환 팀명 기준으로 order by
     */
    @GetMapping("/teams")
    public ResponseEntity<SuccessResponse<List<TeamResponse>>> getTeams() {
        List<TeamResponse> teamResponses = teamService.findByTeams();

        return ResponseEntity.ok(
            SuccessResponse.of(TeamSuccessCode.TEAM_SUCCESS_FOUND, teamResponses)
        );
    }

    /**
     * 팀 페이지네이션 API
     *
     * @param page 몇 번째 페이지인지
     * @param size 한 페이지당 몇개의 크기를 반환 받을 것인지
     * @param region 팀의 연고지 ( Korea, China )
     * @param league 팀의 리그 ( LCK, LPL... )
     * @param keyword 검색 키워드
     *
     * @return ResponseEntity<SuccessResponse<Page<TeamResponse>>>
     * @throws TeamException 조회 실패시 반환
     */
    @GetMapping("/teams/paged")
    public ResponseEntity<SuccessResponse<Page<TeamResponse>>> getPlayersWithPaging(
        @RequestParam @Min(0) int page,
        @RequestParam @Min(1) @Max(100) int size,
        @RequestParam(required = false) String region,
        @RequestParam(required = false) League league,
        @RequestParam(required = false) Boolean isDisbanded,
        @RequestParam(required = false) String keyword
    ) {

        Page<TeamResponse> teamResponsePage =
            teamService.findTeamWithPaging(
                page,
                size,
                region,
                league,
                isDisbanded,
                keyword
            );

        return ResponseEntity.ok(
            SuccessResponse.of(TeamSuccessCode.TEAM_SUCCESS_FOUND, teamResponsePage));
    }

}
