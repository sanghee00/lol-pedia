package com.example.lolpedia.playermatchstats.controller;

import com.example.lolpedia.matches.entity.Matches;
import com.example.lolpedia.matches.repository.MatchRepository;
import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.player.repository.PlayerRepository;
import com.example.lolpedia.playermatchstats.entity.PlayerMatchStats;
import com.example.lolpedia.playermatchstats.enums.Side;
import com.example.lolpedia.playermatchstats.repository.PlayerMatchStatsRepository;
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
class PlayerMatchStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMatchStatsRepository playerMatchStatsRepository;

    @Autowired
    private MatchRepository matchesRepository;

    private Team team1;
    private Team team2;
    private Player player1;
    private Matches match1;
    private final PlayerMatchStats[] playerMatchStatsArray = new PlayerMatchStats[10];

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

        player1 = Player.builder()
            .playerCountry("Kr")
            .playerImageUrl("url")
            .playerKey("Faker")
            .playerName("Faker")
            .playerNameFull("Faker")
            .playerNativeName("이상혁")
            .mainPosition(Position.MID)
            .team(team1)
            .build();
        playerRepository.save(player1);

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
            .matchCode("test-game-code-123-1")
            .gameCode("test-game-code-123")
            .teamABans("[]").teamBBans("[]").teamAPicks("[]").teamBPicks("[]")
            .teamAPlayers("[]").teamBPlayers("[]")
            .build();
        matchesRepository.save(match1);

        for (int i = 0; i < 10; i++) {
            PlayerMatchStats playerMatchStats = PlayerMatchStats.builder()
                .assists(1)
                .cs(1)
                .damage(1)
                .deaths(1)
                .championPlayed("champion" + i)
                .kills(1)
                .gold(1000)
                .matchCode(match1.getMatchCode())
                .gameCode(match1.getGameCode()) // Match와 동일한 gameCode 사용
                .playerName("Faker")
                .runes("run" + i)
                .side(Side.BLUE)
                .summonerSpells("Spells" + i)
                .trinket("trinket" + i)
                .visionScore(100)
                .playerId(player1)
                .matchId(match1)
                .build();

            playerMatchStatsArray[i] = playerMatchStats;
            playerMatchStatsRepository.saveAndFlush(playerMatchStats);
        }
    }


    @DisplayName("유저 개인 매치 정보 플레이어 아이디로 10개 조회 성공")
    @Test
    void getPlayerMatchStatisticsByPlayerIdSuccess() throws Exception {
        // given
        Long playerId = player1.getPlayerId();

        // when & then
        mockMvc.perform(
                get("/playermatchstatus")
                    .param("playerId", playerId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.length()", is(10)))
            .andExpect(jsonPath("$.result[0].player_name").value("Faker"));
    }

    @DisplayName("유저 개인 매치 정보 플레이어 아이디로 조회 실패")
    @Test
    void getPlayerMatchStatisticsByPlayerIdNotFound() throws Exception {
        // given
        String NON_EXISTENT_PLAYER_ID = "9999999999";

        // when & then
        mockMvc.perform(
                get("/playermatchstatus")
                    .param("playerId", NON_EXISTENT_PLAYER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("매치 아이디로 매치 조회 성공")
    void getPlayerMatchStatisticsByMatchIdSuccess() throws Exception {
        // given
        Long matchId = match1.getMatchId();

        // when & then
        mockMvc.perform(
                get("/playermatchstatus")
                    .param("matchId", matchId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.length()", is(10)))
            .andExpect(jsonPath("$.result[0].match_id").value(match1.getMatchId()))
            .andExpect(jsonPath("$.result[0].champion_played", is(playerMatchStatsArray[0].getChampionPlayed())));
    }

    @DisplayName("유저 개인 매치 정보 플레이어 아이디로 조회 실패 (404 Not Found)")
    @Test
    void getPlayerMatchStatisticsByMatchIdNotFound() throws Exception {
        // given
        // 존재하지 않을 것이 확실한 임의의 플레이어 ID (아주 큰 숫자 등)
        String NON_EXISTENT_MATCH_ID = "9999999999";

        // when & then
        mockMvc.perform(
                get("/playermatchstatus")
                    .param("matchId", NON_EXISTENT_MATCH_ID)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

}
