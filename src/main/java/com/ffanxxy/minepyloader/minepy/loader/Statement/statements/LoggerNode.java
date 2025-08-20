package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LoggerNode extends MethodNode {

    public LoggerNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodParametersBuilder getPlansBuilder(MethodParametersBuilder builder) {
       builder.add("log", DataType.OBJECT, DataType.OBJECT);
        builder.add("warn", DataType.OBJECT, DataType.OBJECT);
        builder.add("err", DataType.OBJECT, DataType.OBJECT);
        builder.add("error", DataType.OBJECT, DataType.OBJECT);
       return builder;
    }

    @Override
    public @NotNull Variable<?> run(String plan, List<Variable<?>> variables) {
        Logger logger = LoggerFactory.getLogger(variables.get(0).toString());

        switch (plan) {
            case "log":
                logger.info(variables.get(1).toString());
                break;
            case "warn":
                logger.warn(variables.get(1).toString());
                break;
            case "err", "error":
                logger.error(variables.get(1).toString());
                break;
        }
        return Variable.VOID();
    }
}
