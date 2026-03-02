package com.example.lolpedia.player.service;

import com.example.lolpedia.player.dto.PlayerInfoResponse;
import com.example.lolpedia.player.dto.PlayerResponse;
import com.example.lolpedia.player.dto.PlayerSeasonRecentMatchResponse;
import com.example.lolpedia.player.dto.PlayerSeasonStatsResponse;
import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.PlayerErrorCode;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.player.exception.PlayerException;
import com.example.lolpedia.player.repository.PlayerRepository;
import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private static final int RECENT_MATCH_LIMIT = 5;

    private final PlayerRepository playerRepository;

    // ** Player이름으로 찾기
    public PlayerResponse findPlayerByPlayerName(String name) {
        return findPlayer(() -> playerRepository.findByPlayerName(name));
    }

    // ** Player아이디로 찾기
    public PlayerResponse findPlayerByPlayerId(long id) {
        return findPlayer(() -> playerRepository.findPlayerByPlayerId(id));
    }

    // ** Player 8명 반환
    @Cacheable(cacheNames = "players-top8")
    public List<PlayerResponse> findEightPlayer() {
        List<Player> eightPlayer = playerRepository.findTop8ByOrderByPlayerNameAsc();

        if (eightPlayer.isEmpty()) {
            throw new PlayerException(PlayerErrorCode.PLAYER_NOT_FOUND);
        }

        return PlayerResponse.of(eightPlayer);
    }

    public Page<PlayerResponse> findPlayerWithPaging(
        int page,
        int size,
        String country,
        Position position,
        League league,
        String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Player> spec = (root, query, criteriaBuilder) ->
            criteriaBuilder.conjunction();
        spec = getPlayerSpecification(country, position, league, keyword, spec);

        Page<Player> playerPage = playerRepository.findAll(spec, pageable);

        if (page > 0 && playerPage.isEmpty()) {
            throw new PlayerException(PlayerErrorCode.INVALID_PAGE);
        }

        return playerPage.map(PlayerResponse::of);
    }

    @Cacheable(cacheNames = "player-info", key = "#playerId")
    public PlayerInfoResponse findPlayerInfo(Long playerId) {
        int currentYear = LocalDate.now().getYear(); // 현재 시즌
        LocalDateTime startOfYear = LocalDate.of(currentYear, 1, 1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.of(currentYear, 12, 31).atTime(LocalTime.MAX);

        PlayerSeasonStatsResponse playerSeasonStatsResponse =
            playerRepository.findPlayerSeasonStats(playerId, startOfYear, endOfYear, currentYear)
                .orElseThrow(() -> new PlayerException(PlayerErrorCode.PLAYER_INFO_NOT_FOUND));

        List<PlayerSeasonRecentMatchResponse> playerSeasonRecentMatchResponses =
            playerRepository.findPlayerRecentMatch(playerId, startOfYear, endOfYear, PageRequest.of(0, RECENT_MATCH_LIMIT));
        if (playerSeasonRecentMatchResponses.isEmpty()) {
            throw new PlayerException(PlayerErrorCode.PLAYER_INFO_NOT_FOUND);
        }

        return new PlayerInfoResponse(playerSeasonStatsResponse, playerSeasonRecentMatchResponses);
    }

    private Specification<Player> getPlayerSpecification(
        String country,
        Position position,
        League league,
        String keyword,
        Specification<Player> spec
    ) {
        if (country != null && !country.isBlank()) {
            spec = spec.and(withCountry(country));
        }

        if (position != null) {
            spec = spec.and(withPosition(position));
        }

        if (league != null) {
            spec = spec.and(withLeague(league));
        }

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(searchPlayer(keyword));
        }

        return spec;
    }

    private PlayerResponse findPlayer(Supplier<Optional<Player>> finder) {
        Player player = finder.get()
            .orElseThrow(() -> new PlayerException(PlayerErrorCode.PLAYER_NOT_FOUND));

        return PlayerResponse.of(player);
    }

    private Specification<Player> searchPlayer(String keyword) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            Join<Player, Team> teamJoin = root.join("team", JoinType.LEFT);
            Predicate playerNameLike = cb.like(root.get("playerName"), "%" + keyword + "%");
            Predicate teamNameLike = cb.like(teamJoin.get("teamName"), "%" + keyword + "%");

            return cb.or(playerNameLike, teamNameLike);
        };
    }

    private Specification<Player> withCountry(String country) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("playerCountry"), country);
    }

    private Specification<Player> withPosition(Position position) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("mainPosition"), position);
    }

    private Specification<Player> withLeague(League league) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.join("team").get("league"), league);
    }

}
