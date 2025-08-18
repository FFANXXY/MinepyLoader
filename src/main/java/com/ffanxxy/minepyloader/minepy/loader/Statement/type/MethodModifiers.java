package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

import java.util.Arrays;

public enum MethodModifiers {

    LOAD("load");

    private final String name;

    MethodModifiers(String name) {
        this.name = name;
    }

    public static MethodModifiers fromName(String name) {
        for (MethodModifiers modifier : values()) {
            if (modifier.name.equals(name)) {
                return modifier;
            }
        }
        throw new IllegalArgumentException("Invalid method modifiers: " + name);
    }

    public static boolean isModifier(String name) {
        return Arrays.stream(values()).anyMatch(
                accessModifiers -> accessModifiers.name.equals(name)
        );
    }
}
