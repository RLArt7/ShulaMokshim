package com.example.harelavikasis.shulamokshim.MainApp.utils;

/**
 * Created by harelavikasis on 11/12/2016.
 */

public enum Level {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String name;

    private Level(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
