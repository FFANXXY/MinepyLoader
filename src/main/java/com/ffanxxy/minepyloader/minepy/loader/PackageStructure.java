package com.ffanxxy.minepyloader.minepy.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通过列表构建层级型的包结构，例如{@code ["java", "util", "List"]} 与 {@code java.util.List} 之间的转化
 */
public class PackageStructure {
    private final List<String> structure;

    public PackageStructure(List<String> structure) {
        this.structure = new ArrayList<>(structure.stream().map(String::trim).filter(s->!s.isEmpty()).toList());
    }

    public PackageStructure(String pack) {
        this.structure = new ArrayList<>(Arrays.stream(pack.split("\\.")).map(String::trim).filter(s->!s.isEmpty()).toList());
    }

    public static PackageStructure create(ScriptPackage scriptPackage) {return new PackageStructure(scriptPackage.toString());}

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

    /**
     * 截取包结构
     * @param length 若<0，则删掉最后{@code |length|}个结构，例如{@code aaa.bbb.ccc}{@code .subList(-1)}则删掉最后一个结构变为{@code aaa.bbb}
     * @return 新的结构
     */
    public PackageStructure subList(int length) {
        if(length < 0) length = structure.size() + length;
        return new PackageStructure(structure.subList(0, length));
    }

    public PackageStructure subList(int begin, int over) {
        return new PackageStructure(structure.subList(begin, over));
    }

    public int size() {
        return this.structure.size();
    }

    /**
     *
     * @param index -n则获得倒数的序列，例如-1为获得最后一个
     * @return 获得的值
     */
    public String get(int index) {
        if(index < 0) {
            return structure.get(structure.size() + index);
        }
        return structure.get(index);
    }

    public boolean isSameAs(PackageStructure packageStructure) {
        return this.toPackage().isPack(packageStructure.toPackage());
    }

    public PackageStructure join(String new_) {
        var n = new ArrayList<>(this.structure);
        n.add(new_);
        return new PackageStructure(n);
    }

    public boolean isSameAs(ScriptPackage package_) {
        return this.toPackage().isSamePackage(package_);
    }
}
