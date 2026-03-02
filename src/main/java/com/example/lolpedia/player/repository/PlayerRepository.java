package com.example.lolpedia.player.repository;

import com.example.lolpedia.player.dto.PlayerSeasonRecentMatchResponse;
import com.example.lolpedia.player.dto.PlayerSeasonStatsResponse;
import com.example.lolpedia.player.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {
    Optional<Player> findByPlayerName(String playerName);

    Optional<Player> findPlayerByPlayerId(Long playerId);

    @Query(
        "SELECT new com.example.lolpedia.player.dto.PlayerSeasonStatsResponse(" +
            "  p.playerName," +
            "  p.playerNativeName, " +
            "  p.mainPosition, " +
            "  p.playerCountry," +
            "  p.playerImageUrl, " +
            "  t.teamName," +
            "  ph.startContractYear, " +
            "  ph.endContractYear, " +
            "  SUM(pms.kills), " +
            "  SUM(pms.deaths), " +
            "  SUM(pms.assists), " +
            "  SUM(pms.cs), " +
            "  SUM(" +
            "    CAST(FUNCTION('SUBSTRING_INDEX', m.gameLength, ':', 1) AS long) * 60 + " +
            "    CAST(FUNCTION('SUBSTRING_INDEX', m.gameLength, ':', -1) AS long)" +
            "  ), " +
            "  SUM(CASE WHEN pms.side = 'BLUE' THEN m.teamAKills ELSE m.teamBKills END), " +
            "  SUM(CASE WHEN m.winnerTeam = p.team THEN 1L ELSE 0L END), " +
            "  COUNT(pms) " +
            ") " +
            "FROM PlayerMatchStats pms " +
            "JOIN pms.matchId m " +
            "JOIN pms.playerId p " +
            "JOIN p.team t " +
            "LEFT JOIN PlayerHistory ph ON ph.player = p AND ph.team = t " +
            "  AND :year BETWEEN CAST(ph.startContractYear AS integer) AND CAST(ph.endContractYear AS integer) " +
            "WHERE p.playerId = :playerId " +
            "  AND m.matchDate BETWEEN :startOfYear AND :endOfYear " +
            "GROUP BY p.playerId, p.playerName, p.playerNativeName, p.playerCountry, " +
            "         p.playerImageUrl, t.teamName, ph.startContractYear, ph.endContractYear"
    )
    Optional<PlayerSeasonStatsResponse> findPlayerSeasonStats(
        @Param("playerId") Long playerId,
        @Param("startOfYear") LocalDateTime startOfYear,
        @Param("endOfYear") LocalDateTime endOfYear,
        @Param("year") int year);

    @EntityGraph(attributePaths = "team")
    List<Player> findTop8ByOrderByPlayerNameAsc();

    @Override
    @EntityGraph(attributePaths = "team")
    Page<Player> findAll(Specification<Player> spec, Pageable pageable);

    @Query(
        "SELECT new com.example.lolpedia.player.dto.PlayerSeasonRecentMatchResponse(" +
            "  wt.teamId, " +
            "  p.team.teamId, " +
            "  m.teamAScore, " +
            "  m.teamBScore, " +
            "  m.matchDate, " +
            "  m.tournament, " +
            "  m.teamAName, " +
            "  m.teamBName, " +
            "  CAST(" +
            "    CAST(FUNCTION('SUBSTRING_INDEX', m.gameLength, ':', 1) AS long) * 60 + " +
            "    CAST(FUNCTION('SUBSTRING_INDEX', m.gameLength, ':', -1) AS long) " +
            "  AS long), " +
            "  CAST(pms.kills AS long), " +
            "  CAST(pms.deaths AS long), " +
            "  CAST(pms.assists AS long), " +
            "  pms.championPlayed, " +
            "  CAST(pms.cs AS long) " +
            ") " +
            "FROM PlayerMatchStats pms " +
            "JOIN pms.matchId m " +
            "JOIN pms.playerId p " +
            "JOIN p.team t " +
            "LEFT JOIN m.winnerTeam wt " +
            "WHERE p.playerId = :playerId " +
            "AND m.matchDate BETWEEN :startOfYear AND :endOfYear " +
            "ORDER BY m.matchDate DESC"
    )
    List<PlayerSeasonRecentMatchResponse> findPlayerRecentMatch(
        @Param("playerId") Long playerId,
        @Param("startOfYear") LocalDateTime startOfYear,
        @Param("endOfYear") LocalDateTime endOfYear,
        Pageable pageable
    );
}
