package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierInputContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierOutput;

public class SignalMethodModifier extends MethodModifier{
    public SignalMethodModifier(String word, ModifierType type) {
        super(word, type);
    }

    public SignalMethodModifier(String word) {
        super(word, ModifierType.MODIFIER);
    }

    @Override
    public ModifierOutput modify(ModifierInputContext context) {
        return new ModifierOutput(false);
    }
}
