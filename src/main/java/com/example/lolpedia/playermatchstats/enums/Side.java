package com.example.lolpedia.playermatchstats.enums;

public enum Side {
    BLUE(1, "블루"),
    RED(2, "레드");

    private final int code;
    private final String label;

    Side(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
