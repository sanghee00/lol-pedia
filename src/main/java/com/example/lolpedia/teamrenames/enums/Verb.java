package com.example.lolpedia.teamrenames.enums;

public enum Verb {
    RENAME("이름 변경"),
    ACQUIRE("인수 및 합병"),
    REBRAND("브랜드 변경");

    private final String verb;

    Verb(String verb) {
        this.verb = verb;
    }
}
