package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierInputContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierOutput;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ParserModifier extends MethodModifier implements AccessModifier{
    public ParserModifier(String word) {
        super(word, ModifierType.MODIFIER);
    }

    @Override
    @Deprecated
    public ModifierOutput modify(@Nullable ModifierInputContext context) {
        return null;
    }

    public abstract boolean modify(Minepy.MethodDefiner definer, int sort);
    public abstract Variable<?> prepareVariable(CommandContext<ServerCommandSource> ctx);
}
