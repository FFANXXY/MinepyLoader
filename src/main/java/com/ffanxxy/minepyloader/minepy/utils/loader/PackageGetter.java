package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;

import java.util.List;
import java.util.Map;

public class PackageGetter {
    public static <T> T getFrom(Map<PackageStructure, T> map, PackageStructure structure) {
           for(PackageStructure packageStructure : map.keySet()) {
               if(packageStructure.isSameAs(structure)) return map.get(packageStructure);
           }
           throw new RuntimeException("Can't find the object from: " + structure);
    }

    public static <T> T getFrom(Map<PackageStructure, T> map, String structure) {
        return getFrom(map, PackageStructure.create(structure));
    }

    public static PackageStructure getNameInImport(String name, List<String> imports) {
        for(String imp : imports) {
            PackageStructure structure = new PackageStructure(imp);
            if(structure.getLast().equals("*")) {
                PackageStructure nameStructure = new PackageStructure(name);
                if(nameStructure.size() < 2) continue;
                if(structure.get(-2).equals(nameStructure.get(-2))) return structure.subList(-1).join(nameStructure.get(-1));
            } else {
                if(structure.getLast().equals(name)) return structure;
            }
        }
        throw new RuntimeException("Can't find object in Imports: " + name);
    }
}
