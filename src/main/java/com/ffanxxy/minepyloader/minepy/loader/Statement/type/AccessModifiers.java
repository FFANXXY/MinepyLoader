package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

import java.util.Arrays;

public enum AccessModifiers {
    PUBLIC("public"),
    PRIVATE("private"),
    DEFAULT("default");

    private final String name;

    AccessModifiers(String name) {
        this.name = name;
    }

    public static AccessModifiers fromName(String name) {
        for (AccessModifiers modifier : values()) {
            if (modifier.name.equals(name)) {
                return modifier;
            }
        }
        throw new IllegalArgumentException("Invalid access modifiers: " + name);
    }

    public static boolean isModifier(String name) {
        return Arrays.stream(values()).anyMatch(
                accessModifiers -> accessModifiers.name.equals(name)
        );
    }

    public String getName() {
        return name;
    }
}
