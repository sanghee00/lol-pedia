package com.example.lolpedia.playermatchstats.service;

import com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse;
import com.example.lolpedia.playermatchstats.enums.PlayerMatchStatsErrorCode;
import com.example.lolpedia.playermatchstats.exception.PlayerMatchStatsException;
import com.example.lolpedia.playermatchstats.repository.PlayerMatchStatsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PlayerMatchStats 서비스
 *
 * 최적화 변경사항:
 * - 기존: SELECT * → 엔티티 전체 로딩 → Service에서 수동 DTO 변환 (N+1 문제 발생)
 * - 변경: Repository에서 JPQL 프로젝션으로 필요한 14개 필드만 직접 조회 (엔티티 미로딩)
 * - toResponseList() 메서드 제거 (Repository 레벨에서 DTO 직접 반환)
 */
@Service
@AllArgsConstructor
public class PlayerMatchStatsService {

    private static final int RECENT_MATCH_LIMIT = 10;

    private final PlayerMatchStatsRepository playerMatchStatsRepository;

    public List<PlayerMatchStatsResponse> findPlayerMatchStatisticsByPlayerId(Long playerId) {
        List<PlayerMatchStatsResponse> stats = playerMatchStatsRepository
                .findPlayerMatchStatsByPlayerIdLimit10(playerId, PageRequest.of(0, RECENT_MATCH_LIMIT));

        if (stats.isEmpty()) {
            throw new PlayerMatchStatsException(PlayerMatchStatsErrorCode.PLAYER_MATCH_STATS_NOT_FOUND);
        }

        return stats;
    }

    public List<PlayerMatchStatsResponse> findPlayerMatchStatisticsByMatchId(Long matchId) {
        List<PlayerMatchStatsResponse> playerMatchStats = playerMatchStatsRepository
                .findPlayerMatchStatsResponsesByMatchId(matchId);

        if (playerMatchStats.isEmpty()) {
            throw new PlayerMatchStatsException(PlayerMatchStatsErrorCode.PLAYER_MATCH_STATS_NOT_FOUND);
        }

        return playerMatchStats;
    }

}
