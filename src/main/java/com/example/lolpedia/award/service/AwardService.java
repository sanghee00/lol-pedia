package com.example.lolpedia.award.service;

import com.example.lolpedia.award.dto.AwardResponse;
import com.example.lolpedia.award.entity.Award;
import com.example.lolpedia.award.enums.AwardErrorCode;
import com.example.lolpedia.award.exception.AwardException;
import com.example.lolpedia.award.repository.AwardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AwardService {

    private final AwardRepository awardRepository;

    public List<AwardResponse> findAward(Long playerId) {
        List<Award> awards = awardRepository.findAwardByPlayer_PlayerId(playerId);

        if (awards.isEmpty()) {
            throw new AwardException(AwardErrorCode.AWARD_NOT_FOUND);
        }

        return AwardResponse.from(awards);
    }

}
