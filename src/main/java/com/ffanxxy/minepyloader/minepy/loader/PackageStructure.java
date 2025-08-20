package com.ffanxxy.minepyloader.minepy.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通过列表构建层级型的包结构，例如{@code ["java", "util", "List"]} 与 {@code java.util.List} 之间的转化
 */
public class PackageStructure {
    private List<String> structure;

    public PackageStructure(List<String> structure) {
        this.structure = new ArrayList<>(structure.stream().map(String::trim).filter(s->!s.isEmpty()).toList());
    }

    public PackageStructure(String pack) {
        this.structure = new ArrayList<>(Arrays.stream(pack.split("\\.")).map(String::trim).filter(s->!s.isEmpty()).toList());
    }

    public static PackageStructure create(String pack) {
        return new PackageStructure(pack);
    }

    public ScriptPackage toPackage() {
        return new ScriptPackage(String.join(".", structure));
    }

    @Override
    public String toString() {
        return String.join(".", structure);
    }

    public String getLast() {
        return structure.get(structure.size() -1 );
    }

    public String getFirst() {
        return structure.get(0);
    }

    public void addFirst(String ele) {
        structure.add(0, ele);
    }

    public String get(int index) {
        return structure.get(index);
    }
}
