package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.World.MpyDimension;
import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldNode extends MethodsNode {
    public WorldNode(List<VarGetterNode> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("new", 0, DataType.STRING);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0 ->  Variable.ofWorld("%TEMP", new MpyWorld());
            default -> Variable.VOID();
        };
    }
}
