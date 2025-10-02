package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers;


import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;

import java.util.function.Supplier;

public class ModifiersRegister {

    public PackageStructure structure;
    public ModifiersRegister(PackageStructure structure) {
        this.structure = structure;
    }

    public MethodModifier register(Supplier<MethodModifier> supplier) {
        MethodModifier modifier = supplier.get();
        MethodModifiers.modifierMap.put(structure.join(supplier.get().getWord()), modifier);
        return modifier;
    }
}
