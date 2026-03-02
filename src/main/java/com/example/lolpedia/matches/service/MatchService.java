package com.example.lolpedia.matches.service;

import com.example.lolpedia.matches.dto.MatchInfoResponse;
import com.example.lolpedia.matches.dto.MatchResponse;
import com.example.lolpedia.matches.dto.MatchSummaryResponse;
import com.example.lolpedia.matches.dto.MatchTeamHistoryResponse;
import com.example.lolpedia.matches.enums.MatchErrorCode;
import com.example.lolpedia.matches.exception.MatchException;
import com.example.lolpedia.matches.repository.MatchRepository;
import com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse;
import com.example.lolpedia.playermatchstats.enums.PlayerMatchStatsErrorCode;
import com.example.lolpedia.playermatchstats.exception.PlayerMatchStatsException;
import com.example.lolpedia.playermatchstats.repository.PlayerMatchStatsRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59);
    private static final int TEAM_HISTORY_LIMIT = 6;

    private final MatchRepository matchRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;

    // 날짜로 조회시 그날 24시간 안에 있는 날짜 조회
    public List<MatchResponse> findMatchByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(END_OF_DAY);

        List<MatchResponse> matches = matchRepository
            .findMatchResponsesByPeriod(startOfDay, endOfDay, Pageable.unpaged())
            .getContent();
        validateMatchResponsesNotEmpty(matches);

        return matches;
    }

    public List<MatchTeamHistoryResponse> findMatchByTeamId(Long teamId) {
        Pageable limitSix = PageRequest.of(0, TEAM_HISTORY_LIMIT);

        List<MatchTeamHistoryResponse> matches = Stream.concat(
                matchRepository.findMatchHistoryByTeamAId(teamId, limitSix).stream(),
                matchRepository.findMatchHistoryByTeamBId(teamId, limitSix).stream()
            )
            .sorted(Comparator.comparing(MatchTeamHistoryResponse::matchDate).reversed())
            .limit(TEAM_HISTORY_LIMIT)
            .toList();

        if (matches.isEmpty()) {
            throw new MatchException(MatchErrorCode.MATCH_NOT_FOUND);
        }

        return matches;
    }

    // TODO: 추후 변경 및 삭제
    public Page<MatchResponse> findMatchByPeriod(LocalDate startDate, LocalDate endDate, int page, int size) {
        if (endDate.isBefore(startDate)) {
            throw new MatchException(MatchErrorCode.MATCH_PERIOD_INVALID);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(END_OF_DAY);

        Pageable pageable = PageRequest.of(page, size);
        Page<MatchResponse> matches = matchRepository.findMatchResponsesByPeriod(startDateTime, endDateTime, pageable);

        if (matches.isEmpty()) {
            throw new MatchException(MatchErrorCode.MATCH_NOT_FOUND);
        }

        return matches;
    }

    @Cacheable(cacheNames = "match-series-period", key = "#startDate + '::' + #endDate + '::' + #page + '::' + #size")
    public Page<MatchSummaryResponse> findSeriesSummariesInPeriod(
        LocalDate startDate, LocalDate endDate, int page,
        int size
    ) {
        if (endDate.isBefore(startDate)) {
            throw new MatchException(MatchErrorCode.MATCH_PERIOD_INVALID);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(END_OF_DAY);

        Pageable pageable = PageRequest.of(page, size);
        Page<MatchSummaryResponse> matches = matchRepository
            .findSeriesSummariesInPeriodNative(startDateTime, endDateTime, pageable)
            .map(MatchSummaryResponse::from);
        if (matches.isEmpty()) {
            throw new MatchException(MatchErrorCode.MATCH_NOT_FOUND);
        }

        return matches;
    }

    @Cacheable(cacheNames = "match-info", key = "#matchCode")
    public MatchInfoResponse findMatchesInfo(String matchCode) {
        List<MatchResponse> matches = matchRepository.findMatchResponsesByMatchCode(matchCode);
        if (matches.isEmpty()) {
            throw new MatchException(MatchErrorCode.MATCH_NOT_FOUND);
        }

        List<PlayerMatchStatsResponse> playerMatchStats = playerMatchStatsRepository
            .findPlayerMatchStatsResponsesByMatchCode(matchCode);
        if (playerMatchStats.isEmpty()) {
            throw new PlayerMatchStatsException(PlayerMatchStatsErrorCode.PLAYER_MATCH_STATS_NOT_FOUND);
        }

        return new MatchInfoResponse(matches, playerMatchStats);
    }

    private void validateMatchResponsesNotEmpty(List<MatchResponse> matches) {
        if (matches.isEmpty()) {
            throw new MatchException(MatchErrorCode.MATCH_NOT_FOUND);
        }
    }

}
