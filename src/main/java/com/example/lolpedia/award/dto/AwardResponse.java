package com.example.lolpedia.award.dto;

import com.example.lolpedia.award.entity.Award;

import java.util.List;

public record AwardResponse(
    Long awardId,
    Long playerId,
    String competition,
    String title,
    String awardYear,
    String awardLogo
) {

    public static List<AwardResponse> from(List<Award> awards) {

        return awards.stream()
            .map(award -> new AwardResponse(
                award.getAwardId(),
                award.getPlayer().getPlayerId(),
                award.getCompetition(),
                award.getTitle(),
                award.getAwardYear(),
                award.getAwardLogo()
            ))
            .toList();
    }

}
