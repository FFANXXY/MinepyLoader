package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers;


import com.ffanxxy.minepyloader.minepy.loader.Loader.MethodExecutor;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierInputContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierOutput;
import com.ffanxxy.minepyloader.minepy.utils.exception.ModifierException;
import com.ffanxxy.minepyloader.minepy.utils.loader.PackageGetter;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MethodModifiers {
    public static Map<PackageStructure, MethodModifier> modifierMap = new HashMap<>();
    public static ModifiersRegister register = new ModifiersRegister(PackageStructure.create("mpy"));

    public static MethodModifier get(PackageStructure structure) {
        return PackageGetter.getFrom(modifierMap, structure);
    }

    public static MethodModifier LOAD = register.register(() -> new SignalMethodModifier("load"));
    public static MethodModifier INGAME = register.register(() -> new SignalMethodModifier("ingame"));

    public static MethodModifier EXECUTABLE = register.register(ExecutableModifier::new);
    static class ExecutableModifier extends ParserModifier {
        public ExecutableModifier() {
            super("executable");
        }

        @Override
        public boolean modify(Minepy.MethodDefiner definer, int sort) {
            if(definer.parameters().size() < sort + 1) throw new ModifierException("[inside] Method \" " + definer.name() + " \" has \"Executable Modifier\" in " + sort
                    + ". It need PLAYER. In fact: None");

            if(definer.parameters().get(sort).dataType.isSameTypeAs(DataType.PLAYER)) {
                return true;
            } else {
                throw new ModifierException("[inside] Method " + definer.name() + " has \"Executable Modifier\" in" + sort
                + ". It need PLAYER. Actually, the variable type for the sort order is " + definer.parameters().get(sort));
            }
        }

        @Override
        public Variable<?> prepareVariable(CommandContext<ServerCommandSource> ctx) {
            if(ctx.getSource().getPlayer() != null) {
                return Variable.ofPlayer("%Temp", ctx.getSource().getPlayer());
            } else {
                throw new RuntimeException("[mpy inside] Executor Modifier need player");
            }
        }

    }

    public static MethodModifier PUBLIC = register.register(PublicModifier::new);
    public static MethodModifier DEFAULT = register.register(PublicModifier::new);
    public static MethodModifier PRIVATE = register.register(PrivateModifier::new);

    static class PublicModifier extends MethodModifier implements AccessModifier {

        public PublicModifier() {
            super("public", ModifierType.ACCESS);
        }

        @Override
        public ModifierOutput modify(ModifierInputContext context) {
            return new ModifierOutput(false);
        }
    }
    static class PrivateModifier extends MethodModifier implements AccessModifier {

        public PrivateModifier() {
            super("private", ModifierType.ACCESS);
        }

        @Override
        public ModifierOutput modify(ModifierInputContext context) {
            if( context.executor().getType() == MethodExecutor.ExecutorType.COMMAND ){
                return new ModifierOutput(true);
            } else {
                return new ModifierOutput(
                        PackageStructure.create(context.executor().getMsg()).isSameAs(context.thisPackage())
                );
            }
        }
    }
}
