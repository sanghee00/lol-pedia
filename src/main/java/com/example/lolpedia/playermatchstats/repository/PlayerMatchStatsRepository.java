package com.example.lolpedia.playermatchstats.repository;

import com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse;
import com.example.lolpedia.playermatchstats.entity.PlayerMatchStats;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {

    /**
     * 플레이어 ID로 최근 10개 매치 스탯 조회 (JPQL 프로젝션)
     * 기존: SELECT pms.* (네이티브 쿼리, 전체 엔티티 + N+1 문제)
     * 변경: 필요한 14개 필드만 직접 프로젝션 (불필요한 visionScore, summonerSpells, runes, trinket, matchCode, gameCode 제거)
     */
    @Query("""
            SELECT new com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse(
                pms.statId,
                pms.matchId.matchId,
                CASE WHEN p.team IS NOT NULL THEN p.team.teamId ELSE NULL END,
                pms.playerName,
                p.mainPosition,
                pms.side,
                pms.championPlayed,
                pms.kills,
                pms.deaths,
                pms.assists,
                pms.damage,
                pms.gold,
                pms.cs,
                p.playerImageUrl
            )
            FROM PlayerMatchStats pms
            JOIN pms.playerId p
            LEFT JOIN p.team
            JOIN pms.matchId m
            WHERE p.playerId = :playerId
            ORDER BY m.matchDate DESC
            """)
    List<PlayerMatchStatsResponse> findPlayerMatchStatsByPlayerIdLimit10(
            @Param("playerId") Long playerId, Pageable pageable);

    /**
     * 매치 ID로 플레이어 스탯 조회 (JPQL 프로젝션)
     * 기존: 엔티티 전체 로딩 + Service에서 수동 DTO 변환 (N+1 문제)
     * 변경: 필요한 14개 필드만 직접 프로젝션
     */
    @Query("""
            SELECT new com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse(
                pms.statId,
                m.matchId,
                CASE WHEN p.team IS NOT NULL THEN p.team.teamId ELSE NULL END,
                pms.playerName,
                p.mainPosition,
                pms.side,
                pms.championPlayed,
                pms.kills,
                pms.deaths,
                pms.assists,
                pms.damage,
                pms.gold,
                pms.cs,
                p.playerImageUrl
            )
            FROM PlayerMatchStats pms
            JOIN pms.playerId p
            JOIN pms.matchId m
            LEFT JOIN p.team
            WHERE m.matchId = :matchId
            ORDER BY
            pms.side ASC,
            CASE p.mainPosition
               WHEN 'TOP' THEN 1
               WHEN 'JGL' THEN 2
               WHEN 'MID' THEN 3
               WHEN 'BOT' THEN 4
               WHEN 'SPT' THEN 5
               ELSE 6
            END ASC
            """)
    List<PlayerMatchStatsResponse> findPlayerMatchStatsResponsesByMatchId(@Param("matchId") Long matchId);

    /**
     * 매치 코드로 플레이어 스탯 조회 (JPQL 프로젝션 - 경량화)
     * 기존 21개 필드 → 14개 필드 (33% 절감)
     * 제거: playerId, matchCode, gameCode, visionScore, summonerSpells, runes, trinket
     */
    @Query("""
            SELECT new com.example.lolpedia.playermatchstats.dto.PlayerMatchStatsResponse(
                pms.statId,
                pms.matchId.matchId,
                CASE WHEN p.team IS NOT NULL THEN p.team.teamId ELSE NULL END,
                pms.playerName,
                p.mainPosition,
                pms.side,
                pms.championPlayed,
                pms.kills,
                pms.deaths,
                pms.assists,
                pms.damage,
                pms.gold,
                pms.cs,
                p.playerImageUrl
            )
            FROM PlayerMatchStats pms
            JOIN pms.playerId p
            LEFT JOIN p.team
            WHERE pms.matchCode = :matchCode
            ORDER BY
            pms.side ASC,
            CASE p.mainPosition
               WHEN 'TOP' THEN 1
               WHEN 'JGL' THEN 2
               WHEN 'MID' THEN 3
               WHEN 'BOT' THEN 4
               WHEN 'SPT' THEN 5
               ELSE 6
            END ASC
            """)
    List<PlayerMatchStatsResponse> findPlayerMatchStatsResponsesByMatchCode(@Param("matchCode") String matchCode);
}
