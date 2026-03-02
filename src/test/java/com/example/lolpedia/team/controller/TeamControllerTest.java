package com.example.lolpedia.team.controller;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.player.repository.PlayerRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Team[] teams = new Team[8];
    private Player player1;

    private Team creatTeam(int index) {
        return Team.builder()
            .teamKey("T1" + index)
            .teamLogoUrl("url" + index)
            .teamName("T1" + index)
            .teamNameShort("T1" + index)
            .isDisbanded(false)
            .league(League.LCK)
            .region("KR")
            .build();
    }

    @BeforeEach
    void setUp() {

        for (int i = 0; i < 8; i++) {
            Team team = creatTeam(i);
            teams[i] = team;
            teamRepository.save(team);
        }

        player1 = Player.builder()
            .team(teams[0])
            .mainPosition(Position.MID)
            .playerNativeName("이상혁")
            .playerNameFull("Lee Sang-hyeok (이상혁)")
            .playerName("Faker")
            .playerKey("Faker")
            .playerImageUrl("url")
            .playerCountry("kr")
            .build();
        playerRepository.save(player1);

    }

    @Test
    @DisplayName("팀 이름으로 조회 시 성공")
    void getTeamByNameSuccess() throws Exception {
        // given
        String TEAM_NAME = teams[0].getTeamName();

        // when & then
        mockMvc.perform(
                get("/team-name")
                    .param("teamName", TEAM_NAME)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.team_name").value((teams[0].getTeamName())));
    }

    @Test
    @DisplayName("존재하지 않는 팀 이름으로 조회 시 404 에러 응답")
    void getTeamByNameNotFound() throws Exception {
        // given
        String TEAM_NAME = "NoSuchTeamNameBJD2";

        // when & then
        mockMvc.perform(
                get("/team-name")
                    .param("teamName", TEAM_NAME)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("존재하지 않는 팀"));

    }

    @Test
    @DisplayName("팀 id로 조회 시 성공")
    void getTeamByIdSuccess() throws Exception {
        // given
        Long TEAM_ID = teams[0].getTeamId();

        // when & then
        mockMvc.perform(
                get("/team-id")
                    .param("teamId", TEAM_ID.toString())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.team_name").value(teams[0].getTeamName()))
            .andExpect(jsonPath("$.result.team_id").value(teams[0].getTeamId()));
    }

    @Test
    @DisplayName("존재하지 않는 팀 id로 조회 시 404 에러 응답")
    void getTeamByIdNotFound() throws Exception {
        // given
        String TEAM_ID = "142342";

        // when & then
        mockMvc.perform(
            get("/team-id")
                .param("teamId", TEAM_ID)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("존재하지 않는 팀"));
    }

    @Test
    @DisplayName("8개의 팀 조회")
    public void getTeams() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(8)));
    }

    @Test
    @DisplayName("팀 목록 페이징 조회 성공")
    public void getTeamWithPagingSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)));
    }

    @Test
    @DisplayName("존재하지 않는 페이지 조회 시 팀 목록 400 에러 응답")
    public void getTeamsWithPagingInvalidPage() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "10")
                    .param("size", "4")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("팀 목록 페이징 연고지(나라)로 필터 조회 성공")
    void getTeamsWithPagingByRegionSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("region", "KR")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].region").value("KR"));
    }

    @Test
    @DisplayName("팀 목록 페이징 지역 리그로 필터 조회 성공")
    void getTeamsWithPagingByLeagueSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("league", "LCK")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].league").value("LCK"));
    }

    @Test
    @DisplayName("팀 목록 페이징 모든 조건 필터로 조회 성공")
    void getTeamsWithPagingByLeagueAndCountryAndLeagueSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("region", "KR")
                    .param("league", "LCK")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].league").value("LCK"))
            .andExpect(jsonPath("$.result.content[0].region").value("KR"));
    }

    @Test
    @DisplayName("이상한 값으로 페이지 조회 시 팀 페이지 빈 값 반환")
    public void getTeamsWithPagingNonExistentValueNotFound() throws Exception {
        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("region", "aNonExistentCountry")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.total_elements", is(0)))
            .andExpect(jsonPath("$.result.total_pages", is(0)));
    }

    @Test
    @DisplayName("페이징 팀 목록 키워드 검색 시 API 검색 성공")
    public void getTeamsWithPagingByPlayerKeywordSuccess() throws Exception {
        // given
        String SEARCH_TEAM = "T1";

        // when & then
        mockMvc.perform(
                get("/teams/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("keyword", SEARCH_TEAM)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].team_name").value(containsString(teams[0].getTeamName())));
    }

}
