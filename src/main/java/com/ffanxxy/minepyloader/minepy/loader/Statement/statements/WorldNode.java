package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.MethodNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldNode extends MethodNode {
    public WorldNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodParametersBuilder getPlansBuilder(MethodParametersBuilder builder) {
        builder.add("new", DataType.STRING);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(String plan, List<Variable<?>> variables) {
        return switch (plan) {
            case "new" ->  {
                World world;
                String worldName = variables.get(0).getAsString().getValue();
                switch (worldName) {
                    case "overworld" -> world = Minepyloader.serverInstance.getWorld(World.OVERWORLD);
                    case "nether" -> world = Minepyloader.serverInstance.getWorld(World.NETHER);
                    case "end" -> world = Minepyloader.serverInstance.getWorld(World.END);
                    default -> throw new RuntimeException("Unknown world type in World.new(" + worldName + ")");
                }
                if(world == null) throw new RuntimeException("You are not in world, But MPY tries to get the Server World!");
                yield Variable.ofWorld("%Temp", world);
            }
            default -> Variable.VOID();
        };
    }
}
