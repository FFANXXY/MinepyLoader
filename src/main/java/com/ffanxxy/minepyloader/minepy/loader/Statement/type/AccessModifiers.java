package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

public enum AccessModifiers {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
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

    public String getName() {
        return name;
    }
}
