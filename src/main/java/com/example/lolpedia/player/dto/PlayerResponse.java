package com.example.lolpedia.player.dto;

import com.example.lolpedia.player.entity.Player;
import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.team.entity.Team;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 플레이어 목록/카드 전용 DTO
 * 프론트엔드에서 실제로 렌더링하는 필드만 포함 (기존 11개 → 10개)
 *
 * 제거된 필드 (프론트엔드 미사용):
 * - playerNameFull (풀네임 - PlayerCard, PlayerSection 등 어디서도 미사용)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record PlayerResponse(
    Long playerId,
    Long teamId,
    String teamName,
    String playerKey,
    String playerName,
    String playerNativeName,
    String playerCountry,
    Position mainPosition,
    String league,
    String playerImageUrl
) {

    private record TeamIdAndTeamNamResult(long teamId, String teamName, String league) { }

    // ** 단일 PlayerDto로 반환
    public static PlayerResponse of(Player player) {
        return toDto(player);
    }

    // ** List PlayerDto로 반환
    public static List<PlayerResponse> of(List<Player> playerList) {
        return playerList.stream().map(PlayerResponse::toDto).toList();
    }

    private static PlayerResponse toDto(Player player) {
        Team team = player.getTeam();
        TeamIdAndTeamNamResult teamIdAndTeamName = getTeamInfoOrDefault(team);
        String playerImageUrl = getPlayerImageOrDefault(player);

        return new PlayerResponse(
            player.getPlayerId(),
            teamIdAndTeamName.teamId(),
            teamIdAndTeamName.teamName(),
            player.getPlayerKey(),
            player.getPlayerName(),
            player.getPlayerNativeName(),
            player.getPlayerCountry(),
            player.getMainPosition(),
            teamIdAndTeamName.league(),
            playerImageUrl
        );
    }

    @Nullable
    private static String getPlayerImageOrDefault(Player player) {
        if (existsPlayerImage(player)) {
            return null;
        }

        return "player_images/" + player.getPlayerImageUrl();
    }

    @NonNull
    private static TeamIdAndTeamNamResult getTeamInfoOrDefault(Team team) {
        if (existsTeam(team)) {
            return new TeamIdAndTeamNamResult(0L, "팀 없음", "리그에 속해 있지 않음");
        }

        return new TeamIdAndTeamNamResult(team.getTeamId(), team.getTeamName(), team.getLeague().toString());
    }

    // 팀이 없는 선수 일수도 있음(예 - 은퇴 또는 휴식)
    private static boolean existsTeam(Team team) {
        return team == null;
    }

    private static boolean existsPlayerImage(Player player) {
        String imageUrl = player.getPlayerImageUrl();
        return imageUrl == null || imageUrl.isBlank();
    }


}
