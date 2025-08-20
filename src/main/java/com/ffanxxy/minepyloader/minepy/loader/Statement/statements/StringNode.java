package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringNode extends MethodNode{
    public StringNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
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
            case "new" -> Variable.ofString("%Temp", variables.get(0).toString());
            default -> Variable.VOID();
        };
    }
}
