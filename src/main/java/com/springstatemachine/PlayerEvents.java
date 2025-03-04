package com.springstatemachine;

import java.util.Arrays;

public enum PlayerEvents {
    FIND_PLAYER("FP"),
    NOT_FIND_PLAYER("NFP"),
    IN_BOUND_ATTACK_RANGE("IBAR"),
    OUT_BOUND_ATTACK_RANGE("OBAR"),
    ;

    private String command;

    PlayerEvents(String command) {
        this.command = command;
    }

    public static PlayerEvents from(String input) {
        return Arrays.stream(values())
                .filter(e -> e.command.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown player state: " + input));
    }
}
