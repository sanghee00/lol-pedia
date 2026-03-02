package com.example.lolpedia.award.controller;

import com.example.lolpedia.award.dto.AwardResponse;
import com.example.lolpedia.award.enums.AwardSuccessCode;
import com.example.lolpedia.award.service.AwardService;
import com.example.lolpedia.global.success.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/award")
@AllArgsConstructor
public class AwardController {

    private final AwardService awardService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<AwardResponse>>> getAward(@RequestParam Long playerId) {
        List<AwardResponse> awardResponses = awardService.findAward(playerId);

        return ResponseEntity.ok(
            SuccessResponse.of(AwardSuccessCode.AWARD_SUCCESS_FOUND, awardResponses)
        );
    }

}
