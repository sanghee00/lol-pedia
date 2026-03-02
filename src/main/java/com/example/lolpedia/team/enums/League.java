package com.example.lolpedia.team.enums;

public enum League {

    LCK("대한민국"),
    LCK_CL("대한민국 2군"),
    LPL("중국"),
    LDL("중국 2군"),
    LCP("퍼시픽"),
    LEC("EMEA"),
    LCS("북중"),
    CBLOL("남미");

    private final String region;

    League(String region) {
        this.region = region;
    }

}
