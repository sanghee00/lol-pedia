package com.example.lolpedia.playermatchstats.dto;

import com.example.lolpedia.player.enums.Position;
import com.example.lolpedia.playermatchstats.enums.Side;

/**
 * 매치 상세 페이지 - 플레이어 매치 스탯 전용 DTO
 * 프론트엔드에서 실제로 렌더링하는 14개 필드만 포함 (기존 21개 → 14개, 33% 절감)
 *
 * 제거된 필드 (프론트엔드 미사용):
 * - playerId (선수 고유 ID - UI에서 미참조)
 * - matchCode, gameCode (매치/게임 코드 - UI에서 미참조)
 * - visionScore (시야 점수 - UI 미구현)
 * - summonerSpells (소환사 주문 - UI 미구현)
 * - runes (룬 정보 - UI 미구현)
 * - trinket (장신구 - UI 미구현)
 */
public record PlayerMatchStatsResponse(
                Long statsId,
                Long matchId,
                Long teamId,
                String playerName,
                Position mainPosition,
                Side side,
                String championPlayed,
                Integer kills,
                Integer deaths,
                Integer assists,
                Integer damage,
                Integer gold,
                Integer cs,
                String playerImageUrl) {
}
