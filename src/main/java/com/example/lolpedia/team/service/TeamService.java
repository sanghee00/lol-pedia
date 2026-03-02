package com.example.lolpedia.team.service;

import com.example.lolpedia.team.dto.TeamResponse;
import com.example.lolpedia.team.dto.TeamResponseWithPlayers;
import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import com.example.lolpedia.team.enums.TeamErrorCode;
import com.example.lolpedia.team.exception.TeamException;
import com.example.lolpedia.team.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private static final int DEFAULT_TEAM_DISPLAY_COUNT = 8;
    private static final String DEFAULT_FEATURED_LEAGUE = "LCK";

    private final TeamRepository teamRepository;

    /**
     * 팀 이름으로 팀 찾기 ( 해당 소속된 팀 플레이어가 포함된 채로 )
     *
     * @param teamName 팀 이름
     * @return TeamResponseWithPlayers
     * @throws TeamException 조회 실패시 반환
     */
    public TeamResponseWithPlayers findByTeamNameWithPlayers(String teamName) {
        Team team = teamRepository.findByTeamName(teamName)
            .orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        return TeamResponseWithPlayers.of(team);
    }

    /**
     * 고유 아이디로 하나의 팀을 찾음 ( 해당 소속의 팀 플레이어가 포함된채로 )
     *
     * @param teamId 팀 고유 아이디
     * @return TeamResponseWithPlayers
     * @throws TeamException 조회 실패 시 반환
     */
    @Cacheable(cacheNames = "team-detail", key = "#teamId")
    public TeamResponseWithPlayers findByTeamIdWithPlayers(Long teamId) {
        Team team = teamRepository.findByTeamId(teamId)
            .orElseThrow(() -> new TeamException(TeamErrorCode.TEAM_NOT_FOUND));

        return TeamResponseWithPlayers.of(team);
    }

    /**
     * 8개의 팀을 반환 ( 팀의 정보만 반환 )
     *
     * @return TeamResponse
     * @throws TeamException 반환
     */
    @Cacheable(cacheNames = "teams-top8")
    public List<TeamResponse> findByTeams() {
        List<Team> teams = teamRepository.findTopTeamsByLeague(
                DEFAULT_FEATURED_LEAGUE, PageRequest.of(0, DEFAULT_TEAM_DISPLAY_COUNT));

        if (teams.isEmpty()) {
            throw new TeamException(TeamErrorCode.TEAM_NOT_FOUND);
        }

        return TeamResponse.from(teams);
    }

    /**
     * 팀 페이지네이션 내부 service 코드
     *
     * @param page    몇 번째 페이지인지
     * @param size    한 페이지당 몇개의 크기를 반환 받을 것인지
     * @param region  팀의 연고지 ( Korea, China )
     * @param league  팀의 리그 ( LCK, LPL... )
     * @param keyword 검색 키워드
     * @return TeamResponse  반환
     * @throws TeamException 조회 실패시 반환
     */
    public Page<TeamResponse> findTeamWithPaging(
        int page, int size, String region, League league, Boolean isDisbanded, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Team> spec = (root, query, criteriaBuilder) ->
            criteriaBuilder.conjunction();
        spec = getTeamSpecification(region, league, keyword, isDisbanded, spec);

        Page<Team> teamPage = teamRepository.findAll(spec, pageable);

        if (page > 0 && teamPage.isEmpty()) {
            throw new TeamException(TeamErrorCode.INVALID_PAGE);
        }

        return teamPage.map(TeamResponse::of);
    }

    private Specification<Team> getTeamSpecification(
        String region,
        League league,
        String keyword,
        Boolean isDisbanded,
        Specification<Team> spec
    ) {
        if (region != null && !region.isBlank()) {
            spec = spec.and(withRegion(region));
        }

        if (league != null) {
            spec = spec.and(withLeague(league));
        }

        if (isDisbanded != null) {
            spec = spec.and(withIsDisbanded(isDisbanded));
        }

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(searchTeam(keyword));
        }

        return spec;
    }

    private Specification<Team> searchTeam(String keyword) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            String pattern = "%" + keyword + "%";
            return cb.like(root.get("teamName"), pattern);  // 팀 이름만 LIKE 검색
        };
    }

    private Specification<Team> withIsDisbanded(Boolean isDisbanded) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("isDisbanded"), isDisbanded);
    }

    private Specification<Team> withRegion(String region) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("region"), region);
    }

    private Specification<Team> withLeague(League league) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("league"), league);
    }

}
