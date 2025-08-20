package com.ffanxxy.minepyloader.minepy.loader;

import java.util.Objects;

/**
 * 目录包类，通过 {@code .mpy}的 {@code #package xxx}来定义。
 * <p> 可以支持多种格式，以代表不同目录,例如 {@code aaa.bbb} {@code aa.bbb.c} {@code abc}
 */
public class ScriptPackage {
    private final String pack;

    public ScriptPackage(String string) {
        this.pack = string;
    }

    private String getPack() {
        return pack;
    }

    /**
     * 是否在同一个包内，传入的String应为 {@code xxx.xxx} 或 {@code xxx} 的格式
     * @param path
     * @return
     */
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
