package com.example.lolpedia.matches.repository;

import com.example.lolpedia.matches.dto.MatchResponse;
import com.example.lolpedia.matches.dto.MatchSummaryProjection;
import com.example.lolpedia.matches.dto.MatchTeamHistoryResponse;
import com.example.lolpedia.matches.entity.Matches;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// TODO: 추후 리펙터링 (Query DSL)
@Repository
public interface MatchRepository extends JpaRepository<Matches, Long> {
    @Query("""
            SELECT new com.example.lolpedia.matches.dto.MatchTeamHistoryResponse(
                m.matchId, wt.teamId, m.teamAId.teamId, m.teamBId.teamId,
                m.tournament, m.matchCode, m.matchDate,
                m.teamAName, m.teamBName,
                m.teamAScore, m.teamBScore,
                m.teamAId.teamLogoUrl, m.teamBId.teamLogoUrl
            )
            FROM Matches m
            LEFT JOIN m.winnerTeam wt
            JOIN m.teamAId
            JOIN m.teamBId
            WHERE m.teamAId.teamId = :teamId
            ORDER BY m.matchDate DESC
            """)
    List<MatchTeamHistoryResponse> findMatchHistoryByTeamAId(@Param("teamId") Long teamId, Pageable pageable);

    @Query("""
            SELECT new com.example.lolpedia.matches.dto.MatchTeamHistoryResponse(
                m.matchId, wt.teamId, m.teamAId.teamId, m.teamBId.teamId,
                m.tournament, m.matchCode, m.matchDate,
                m.teamAName, m.teamBName,
                m.teamAScore, m.teamBScore,
                m.teamAId.teamLogoUrl, m.teamBId.teamLogoUrl
            )
            FROM Matches m
            LEFT JOIN m.winnerTeam wt
            JOIN m.teamAId
            JOIN m.teamBId
            WHERE m.teamBId.teamId = :teamId
            ORDER BY m.matchDate DESC
            """)
    List<MatchTeamHistoryResponse> findMatchHistoryByTeamBId(@Param("teamId") Long teamId, Pageable pageable);

    @Query("""
            SELECT new com.example.lolpedia.matches.dto.MatchResponse(
                m.matchId, wt.teamId, m.teamAId.teamId, m.teamBId.teamId,
                m.tournament, m.matchCode, m.gameName,
                m.patch, m.gameLength, m.matchDate,
                m.teamAName, m.teamBName,
                m.teamABans, m.teamBBans,
                m.teamADragons, m.teamBDragons,
                m.teamABarons, m.teamBBarons,
                m.teamATowers, m.teamBTowers,
                m.teamAGold, m.teamBGold,
                m.teamAKills, m.teamBKills,
                m.teamAScore, m.teamBScore,
                m.teamAId.teamLogoUrl, m.teamBId.teamLogoUrl
            )
            FROM Matches m
            LEFT JOIN m.winnerTeam wt
            JOIN m.teamAId
            JOIN m.teamBId
            WHERE m.matchDate BETWEEN :start AND :end
            ORDER BY m.matchDate DESC
            """)
    Page<MatchResponse> findMatchResponsesByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("""
            SELECT new com.example.lolpedia.matches.dto.MatchResponse(
                m.matchId, wt.teamId, m.teamAId.teamId, m.teamBId.teamId,
                m.tournament, m.matchCode, m.gameName,
                m.patch, m.gameLength, m.matchDate,
                m.teamAName, m.teamBName,
                m.teamABans, m.teamBBans,
                m.teamADragons, m.teamBDragons,
                m.teamABarons, m.teamBBarons,
                m.teamATowers, m.teamBTowers,
                m.teamAGold, m.teamBGold,
                m.teamAKills, m.teamBKills,
                m.teamAScore, m.teamBScore,
                m.teamAId.teamLogoUrl, m.teamBId.teamLogoUrl
            )
            FROM Matches m
            LEFT JOIN m.winnerTeam wt
            JOIN m.teamAId
            JOIN m.teamBId
            WHERE m.matchCode = :matchCode
            ORDER BY m.gameCode
            """)
    List<MatchResponse> findMatchResponsesByMatchCode(@Param("matchCode") String matchCode);

    /**
     * 다전제(Bo3, Bo5) 경기 중 가장 마지막 세트만 추출하여 시리즈 요약 제공
     * - ROW_NUMBER() 윈도우 함수를 사용하여 서브쿼리(Filesort) 원천 차단
     * - COUNT(DISTINCT)를 사용하여 빠르고 안전한 페이징 카운트 계산
     */
    @Query(value = """
            SELECT
                sub.match_id AS matchId,
                sub.match_code AS matchCode,
                sub.match_date AS matchDate,
                sub.tournament AS tournament,
                sub.team_a_name AS teamAName,
                sub.team_b_name AS teamBName,
                sub.team_a_score AS teamAScore,
                sub.team_b_score AS teamBScore,
                ta.team_logo_url AS teamALogoUrl,
                tb.team_logo_url AS teamBLogoUrl
            FROM (
                SELECT match_id, match_code, match_date, tournament,
                       team_a_name, team_b_name, team_a_score, team_b_score, team_a_id, team_b_id,
                       ROW_NUMBER() OVER (PARTITION BY match_code ORDER BY match_date DESC) as rn
                FROM matches
                WHERE match_date >= :start AND match_date < :end
            ) sub
            JOIN team ta ON sub.team_a_id = ta.team_id
            JOIN team tb ON sub.team_b_id = tb.team_id
            WHERE sub.rn = 1
            ORDER BY sub.match_date DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT match_code)
            FROM matches
            WHERE match_date >= :start AND match_date < :end
            """, nativeQuery = true)
    Page<MatchSummaryProjection> findSeriesSummariesInPeriodNative(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

}
