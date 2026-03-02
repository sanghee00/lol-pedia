package com.example.lolpedia.playerhistory.controller;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.player.repository.PlayerRepository;
import com.example.lolpedia.playerhistory.entity.PlayerHistory;
import com.example.lolpedia.playerhistory.repository.PlayerHistoryRepository;
import com.example.lolpedia.team.entity.Team;
import com.example.lolpedia.team.enums.League;
import com.example.lolpedia.team.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PlayerHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerHistoryRepository playerHistoryRepository;

    private Team team1;
    private Player player1;
    private PlayerHistory playerHistory1;

    @BeforeEach
    void setUp() {
        team1 = Team.builder()
            .teamKey("T1")
            .teamLogoUrl("url")
            .teamName("T1")
            .teamNameShort("T1")
            .isDisbanded(false)
            .league(League.LCK)
            .region("KR")
            .build();
        teamRepository.save(team1);

        player1 = Player.builder()
            .team(team1)
            .mainPosition(Position.MID)
            .playerNativeName("이상혁")
            .playerNameFull("Lee Sang-hyeok (이상혁)")
            .playerName("Faker")
            .playerKey("Faker")
            .playerImageUrl("url")
            .playerCountry("kr")
            .build();
        playerRepository.save(player1);

        playerHistory1 = PlayerHistory.builder()
            .player(player1)
            .team(team1)
            .startContractYear("2019-12-03")
            .endContractYear("2026-12-03")
            .build();
        playerHistoryRepository.save(playerHistory1);
    }

    @Test
    @DisplayName("플레이어 id로 기록 조회 성공")
    void getPlayerHistorySuccess() throws Exception {
        // given
        Long playerId = player1.getPlayerId();

        // when & then
        mockMvc.perform(
            get("/playerhistory")
                .param("playerId", playerId.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.player_id").value(player1.getPlayerId()))
            .andExpect(jsonPath("$.result.histories.length()", is(1)));
    }

    @Test
    @DisplayName("존재하지 않는 id로 조회 시 404에러 응답")
    void getPlayerHistoryNotFound() throws Exception {
        // given
        String playerId = "28371827";

        // when & then
        mockMvc.perform(
                get("/playerhistory")
                    .param("playerId", playerId)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }
}
