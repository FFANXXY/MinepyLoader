package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringNode extends MethodsNode {
    public StringNode(List<VarGetterNode> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("new", 0, DataType.STRING);
        builder.add("of", 1, DataType.OBJECT);
        builder.add("lengthOf", 2,DataType.STRING);
        builder.add("charAt", 3, DataType.STRING, DataType.INT);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0,1 -> Variable.ofString("%Temp", args.getString(0));
            case 2 -> Variable.ofInteger("%Temp", args.getString(0).length());
            case 3 -> Variable.ofChar("%Temp", args.getString(0).charAt(args.getInteger(1)));
            default -> Variable.VOID();
        };
    }
}
