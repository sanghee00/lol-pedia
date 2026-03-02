package com.example.lolpedia.player.enums;

public enum Position {

    TOP("탑"),
    JGL("정글"),
    MID("미드"),
    BOT("원딜"),
    SPT("서폿"),
    STREAMER("스트리머"),
    COACH("코치"),
    ANALYST("분석가"),
    MANAGER("매니저"),
    OWNER("구단주");

    private final String role;

    Position(String role) {
        this.role = role;
    }

}
