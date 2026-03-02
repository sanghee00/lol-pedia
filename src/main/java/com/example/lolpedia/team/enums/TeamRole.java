package com.example.lolpedia.team.enums;

public enum TeamRole {

    PLAYER("현역"),
    HEAD_COACH("감독"),
    COACH("코치");

    private final String coachRole;

    TeamRole(String coachRole) {
        this.coachRole = coachRole;
    }

}
