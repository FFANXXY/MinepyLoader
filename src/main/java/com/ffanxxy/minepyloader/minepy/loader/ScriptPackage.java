package com.ffanxxy.minepyloader.minepy.loader;

import java.util.Objects;

public class ScriptPackage {
    private final String pack;

    public ScriptPackage(String string) {
        this.pack = string;
    }

    private String getPack() {
        return pack;
    }

    public boolean isPack(String path) {
        return Objects.equals(pack, path);
    }

    public boolean isPack(ScriptPackage scriptPackage) {
        return Objects.equals(pack, scriptPackage.getPack());
    }

    public boolean isSamePackage(String method) {
        return Objects.equals(pack, method.substring(0,method.lastIndexOf(".")));
    }

    @Override
    public String toString() {
        return this.pack;
    }
}
