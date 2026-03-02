package com.example.lolpedia.matches.controller;

import com.example.lolpedia.global.success.SuccessResponse;
import com.example.lolpedia.matches.dto.MatchInfoResponse;
import com.example.lolpedia.matches.dto.MatchResponse;
import com.example.lolpedia.matches.dto.MatchSummaryResponse;
import com.example.lolpedia.matches.dto.MatchTeamHistoryResponse;
import com.example.lolpedia.matches.enums.MatchSuccessCode;
import com.example.lolpedia.matches.service.MatchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * 당일에 일어난 매치 정보 조회 조건은 날짜
     *
     * @param date 날짜
     * @return ResponseEntity<SuccessResponse<List<MatchResponse>>>
     * @throws com.example.lolpedia.matches.exception.MatchException 매치 조회 실패시
     */
    @GetMapping(value = "/match", params = "date")
    public ResponseEntity<SuccessResponse<List<MatchResponse>>> getMatchByDate(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<MatchResponse> matchResponses = matchService.findMatchByDate(date);

        return ResponseEntity.ok(
            SuccessResponse.of(MatchSuccessCode.MATCH_SUCCESS_FOUND, matchResponses));
    }

    /**
     * 팀 Id로 매치 이력 조회
     *
     * @param teamId 팀 고유 아이디
     * @return ResponseEntity<SuccessResponse<List<MatchTeamHistoryResponse>>>
     * @throws com.example.lolpedia.matches.exception.MatchException 매치 조회 실패시
     */
    @GetMapping(value = "/match", params = "teamId")
    public ResponseEntity<SuccessResponse<List<MatchTeamHistoryResponse>>> getMatchByTeamId(
        @RequestParam Long teamId) {
        List<MatchTeamHistoryResponse> matchResponses = matchService.findMatchByTeamId(teamId);

        return ResponseEntity.ok(
            SuccessResponse.of(MatchSuccessCode.MATCH_SUCCESS_FOUND, matchResponses));
    }

    /**
     * 시작 날짜 ~ 끝나는 날짜에 있는 매치 정보들 조회
     *
     * @param startDate 시작 날짜
     * @param endDate   끝나는 날짜
     * @return ResponseEntity<SuccessResponse<Page<MatchResponse>>>
     * @throws com.example.lolpedia.matches.exception.MatchException 매치 조회 실패시
     */
    @GetMapping(value = "/match", params = {"startDate", "endDate"})
    public ResponseEntity<SuccessResponse<Page<MatchResponse>>> getMatchByPeriod(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "15") @Min(1) @Max(100) int size) {
        Page<MatchResponse> matchResponses = matchService.findMatchByPeriod(startDate, endDate, page, size);

        return ResponseEntity.ok(
            SuccessResponse.of(MatchSuccessCode.MATCH_SUCCESS_FOUND, matchResponses));
    }

    /**
     * 시작 날짜 ~ 끝나는 날짜에 있는 요약 매치 정보들 조회
     *
     * @param startDate 시작 날짜
     * @param endDate   끝나는 날짜
     * @return ResponseEntity<SuccessResponse<Page<MatchSummaryResponse>>>
     * @throws com.example.lolpedia.matches.exception.MatchException 매치 조회 실패시
     */
    @GetMapping(value = "/matches", params = {"startDate", "endDate"})
    public ResponseEntity<SuccessResponse<Page<MatchSummaryResponse>>> getMatchesByPeriod(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "15") @Min(1) @Max(100) int size) {
        Page<MatchSummaryResponse> matchResponses = matchService.findSeriesSummariesInPeriod(startDate, endDate, page, size);

        return ResponseEntity.ok(
            SuccessResponse.of(MatchSuccessCode.MATCH_SUCCESS_FOUND, matchResponses));
    }

    @GetMapping(value = "/match", params = "code")
    public ResponseEntity<SuccessResponse<MatchInfoResponse>> getMatchInfo(@RequestParam("code") String matchCode) {
        MatchInfoResponse matchesInfo = matchService.findMatchesInfo(matchCode);

        return ResponseEntity.ok(
            SuccessResponse.of(MatchSuccessCode.MATCH_INFO_SUCCESS_FOUND, matchesInfo));
    }

}
