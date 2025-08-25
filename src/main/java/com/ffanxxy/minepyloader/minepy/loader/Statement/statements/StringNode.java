package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.MethodNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringNode extends MethodNode {
    public StringNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodParametersBuilder getPlansBuilder(MethodParametersBuilder builder) {
        builder.add("new", DataType.STRING);
        builder.add("of", DataType.OBJECT);
        builder.add("lengthOf",DataType.STRING);
        builder.add("charAt", DataType.STRING, DataType.INT);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(String plan, List<Variable<?>> variables) {
        return switch (plan) {
            case "new", "of" -> Variable.ofString("%Temp", variables.get(0).toString());
            case "lengthOf" -> Variable.ofInteger("%Temp", variables.get(0).toString().length());
            case "charAt" -> Variable.ofChar("%Temp", variables.get(0).toString().charAt(variables.get(1).getAsInt().getValue()));
            default -> Variable.VOID();
        };
    }
}
