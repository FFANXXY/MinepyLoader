package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierInputContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierOutput;

/**
 * 作为单例
 */
public abstract class MethodModifier {
    private ModifierType type;
    private String word;

    public MethodModifier(String word, ModifierType type) {
        this.type = type;
        this.word = word.trim();
    }

    public final ModifierType getType() {
        return type;
    }

    public final String getWord() {
        return this.word;
    }

    public abstract ModifierOutput modify(ModifierInputContext context);

    public enum ModifierType {
        ACCESS,
        MODIFIER;

        public boolean isAccessModifier() {
            return this == ACCESS;
        }
    }
}
