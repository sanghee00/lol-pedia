package com.example.lolpedia.player.controller;

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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    private final Player[] players = new Player[8];
    private Team team1;

    private Player createPlayer(int index) {
        return Player.builder()
            .playerCountry("KR")
            .playerImageUrl("url" + index)
            .playerKey("Faker" + index)
            .playerName("Faker" + index)
            .playerNameFull("Faker" + index)
            .playerNativeName("이상혁" + index)
            .mainPosition(Position.MID)
            .team(team1)
            .build();
    }

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
        teamRepository.save(team1);

        Player player;
        for (int i = 0; i < 8; i++) {
            player = createPlayer(i);

            playerRepository.save(player);
            players[i] = player;
        }
    }

    @Test
    @DisplayName("플레이어 이름으로 조회 성공")
    void getPlayerByNameSuccess() throws Exception {
        // given
        String PLAYER_NAME = players[0].getPlayerName();

        // when & then
        mockMvc.perform(
                get("/player")
                    .param("name", PLAYER_NAME)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.player_name").value(players[0].getPlayerName()))
            .andExpect(jsonPath("$.result.player_id").value(players[0].getPlayerId()));
    }

    @Test
    @DisplayName("존재하지 않는 이름 조회 시 404 에러 응답")
    void getPlayerByNameNotFound() throws Exception {
        // given
        String PLAYER_NAME = "NoSuchPlayer";

        // when & then
        mockMvc.perform(
            get("/player")
                .param("name", PLAYER_NAME)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("플레이어 id로 조회 성공")
    void getPlayerByIdSuccess() throws Exception {
        // given
        Long PLAYER_ID = players[0].getPlayerId();

        // when & then
        mockMvc.perform(
            get("/player")
                .param("id", PLAYER_ID.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.player_name").value(players[0].getPlayerName()))
            .andExpect(jsonPath("$.result.player_id").value(players[0].getPlayerId()));
    }

    @Test
    @DisplayName("존재하지 않는 id 조회 시 404 에러 응답")
    void getPlayerByIdNotFound() throws Exception {
        // given
        String PLAYER_ID = "1483574376";

        // when & then
        mockMvc.perform(
                get("/player")
                    .param("id", PLAYER_ID)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("8명의 플레이어 조회")
    void getPlayers() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.length()", is(8)));
    }

    @Test
    @DisplayName("플레이어 목록 페이징 조회 성공")
    void getPlayersWithPagingSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
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
    @DisplayName("존재하지 않는 페이지 조회 시 400 에러 응답")
    void getPlayersWithPagingInvalidPage() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "10")
                    .param("size", "4")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("플레이어 목록 페이징 나라로 필터 조회 성공")
    void getPlayersWithPagingByCountrySuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("country", "KR")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].player_country").value("KR"));
    }

    @Test
    @DisplayName("플레이어 목록 페이징 포지션으로 필터 조회 성공")
    void getPlayersWithPagingByPositionSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("position", "MID")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].main_position").value("MID"));
    }

    @Test
    @DisplayName("플레이어 목록 페이징 지역 리그로 필터 조회 성공")
    void getPlayersWithPagingByLeagueSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
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
    @DisplayName("플레이어 목록 페이징 모든 조건 필터로 조회 성공")
    void getPlayersWithPagingByLeagueAndCountryAndLeagueSuccess() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("country", "KR")
                    .param("position", "MID")
                    .param("league", "LCK")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content.length()", is(4)))
            .andExpect(jsonPath("$.result.total_pages", is(2)))
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].league").value("LCK"))
            .andExpect(jsonPath("$.result.content[0].main_position").value("MID"))
            .andExpect(jsonPath("$.result.content[0].player_country").value("KR"));
    }

    @Test
    @DisplayName("이상한 값으로 페이지 조회 시 빈 값 반환")
    void getPlayersWithPagingNonExistentValueNotFound() throws Exception {
        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("country", "aNonExistentCountry")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.total_elements", is(0)))
            .andExpect(jsonPath("$.result.total_pages", is(0)));
    }

    @Test
    @DisplayName("페이징 선수들 키워드 검색 시 API 검색 성공")
    void getPlayersWithPagingByPlayerKeywordSuccess() throws Exception {
        // given
        String SEARCH_PLAYER = "Faker";

        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("keyword", SEARCH_PLAYER)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].player_name").value(containsString(SEARCH_PLAYER)));
    }

    @Test
    @DisplayName("페이징 팀으로 키워드 검색 시 API 검색 성공")
    void getPlayersWithPagingByTeamKeywordSuccess() throws Exception {
        // given
        String SEARCH_TEAM = "T1";

        // when & then
        mockMvc.perform(
                get("/players/paged")
                    .param("page", "0")
                    .param("size", "4")
                    .param("keyword", SEARCH_TEAM)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.total_elements", is(8)))
            .andExpect(jsonPath("$.result.content[0].team_name").value(containsString(players[0].getTeam().getTeamName())));
    }
}
