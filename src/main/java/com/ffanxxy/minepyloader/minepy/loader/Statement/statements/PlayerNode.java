package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.MethodNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerNode extends MethodNode {
    public PlayerNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodParametersBuilder getPlansBuilder(MethodParametersBuilder builder) {
        builder.add("new", DataType.WORLD);
        builder.add("kill", DataType.PLAYER);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(String plan, List<Variable<?>> variables) {
        return switch (plan) {
            case "new" -> {
                World world = variables.get(0).getAsWorld().getValue();
                if(world.getPlayers().isEmpty()) yield Variable.ofPlayer("%temp", null);
                yield Variable.ofPlayer("%temp", world.getPlayers().get(0));
            }
            case "kill" -> {
                PlayerEntity player = variables.get(0).getAsPlayer().getValue();
                if(player == null) yield Variable.VOID();
                player.kill();
                yield Variable.VOID();
            }
            default -> Variable.VOID();
        };
    }
}
