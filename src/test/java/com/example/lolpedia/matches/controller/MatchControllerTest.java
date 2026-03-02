package com.example.lolpedia.matches.controller;

import com.example.lolpedia.matches.entity.Matches;
import com.example.lolpedia.matches.repository.MatchRepository;
import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import com.example.lolpedia.team.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Team team1;
    private Team team2;
    private Matches match1;
    private Matches match2;

    @BeforeEach
    void setUp() {
        // given
        team1 = Team.builder()
                .teamName("T1")
                .teamKey("T1")
                .teamNameShort("T1")
                .league(League.LCK)
                .region("KR")
                .teamLogoUrl("url1")
                .isDisbanded(false)
                .build();

        team2 = Team.builder()
                .teamName("GEN")
                .teamKey("GEN")
                .teamNameShort("GEN")
                .league(League.LCK)
                .region("KR")
                .teamLogoUrl("url2")
                .isDisbanded(false)
                .build();

        teamRepository.save(team1);
        teamRepository.save(team2);

        match1 = Matches.builder()
                .teamAId(team1)
                .teamBId(team2)
                .teamAName(team1.getTeamName())
                .teamBName(team2.getTeamName())
                .winnerTeam(team1)
                .winnerTeamName(team1.getTeamName())
                .lossTeam(team2)
                .lossTeamName(team2.getTeamName())
                .matchDate(LocalDateTime.of(2025, 9, 21, 17, 0, 0))
                .teamABans("[]").teamBBans("[]").teamAPicks("[]").teamBPicks("[]")
                .teamAPlayers("[]").teamBPlayers("[]")
                .build();

        match2 = Matches.builder()
                .teamAId(team1)
                .teamBId(team2)
                .teamAName(team1.getTeamName())
                .teamBName(team2.getTeamName())
                .winnerTeam(team2)
                .winnerTeamName(team2.getTeamName())
                .lossTeam(team1)
                .lossTeamName(team1.getTeamName())
                .matchDate(LocalDateTime.of(2025, 9, 29, 17, 0, 0))
                .teamABans("[]").teamBBans("[]").teamAPicks("[]").teamBPicks("[]")
                .teamAPlayers("[]").teamBPlayers("[]")
                .build();

        matchRepository.save(match1);
        matchRepository.save(match2);
    }

    @Test
    @DisplayName("팀 id로 경기 조회 시 성공")
    void getMatchByTeamIdSuccess() throws Exception {
        // given
        String url = "/match";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("teamId", team1.getTeamId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()", is(2)))
                .andExpect(jsonPath("$.result[0].winner_team_id").value(team2.getTeamId()))
                .andExpect(jsonPath("$.result[0].team_a_name", is(team1.getTeamName())))
                .andExpect(jsonPath("$.result[0].team_b_name", is(team2.getTeamName())))
                .andExpect(jsonPath("$.result[1].winner_team_id").value(team1.getTeamId()))
                .andExpect(jsonPath("$.result[1].team_a_name", is(team1.getTeamName())))
                .andExpect(jsonPath("$.result[1].team_b_name", is(team2.getTeamName())));
    }

    @Test
    @DisplayName("존재 하지 않는 id로 경기 조회 시 실패")
    void getMatchByTeamIdNotFound() throws Exception {
        // given
        String NON_EXISTENT_ID = "99999";
        String url = "/match";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("teamId", NON_EXISTENT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("날짜로 조회 시 성공")
    void getMatchByDateSuccess() throws Exception {
        // given
        String url = "/match";

        String date = match1.getMatchDate().toLocalDate().toString();

        // when & then
        mockMvc.perform(
                get(url)
                        .param("date", date)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()", is(1)))
                .andExpect(jsonPath("$.result[0].match_date").value(date));
    }

    @Test
    @DisplayName("경기가 존재 하지 않는 날짜로 조회시 404 에러")
    void getMatchByDateNotFound() throws Exception {
        // given
        String url = "/match";
        String date = "2130-12-02";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("date", date)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주간 날짜 별로 조회 시 성공")
    void getMatchByPeriodSuccess() throws Exception {
        // given
        String url = "/match";
        String startDate = "2025-09-21";
        String endDate = "2025-09-30";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content.length()", is(2)));
    }

    @Test
    @DisplayName("주간 날짜 별로 요약본 조회 성공")
    void getMatchesByPeriodSuccess() throws Exception {
        // given
        String url = "/matches";
        String startDate = "2025-09-21";
        String endDate = "2025-09-30";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content.length()", is(1)));
    }

    @Test
    @DisplayName("종료 날짜보다 시작 날짜 빠를 경우 400에러")
    void getMatchByPeriodBadRequest() throws Exception {
        // given
        String url = "/match";
        String startDate = "2025-09-30";
        String endDate = "2025-09-21";

        // when & then
        mockMvc.perform(
                get(url)
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("종료 날짜는 시작 날짜보다 빠를 수 없습니다."));
    }

}
