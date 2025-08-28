package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggerNode extends MethodsNode {

    public LoggerNode(List<VarGetterNode> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
       builder.add("log" , 0 , DataType.OBJECT, DataType.OBJECT);
        builder.add("warn", 1, DataType.OBJECT, DataType.OBJECT);
        builder.add("err", 2, DataType.OBJECT, DataType.OBJECT);
        builder.add("error", 2, DataType.OBJECT, DataType.OBJECT);
       return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        Logger logger = LoggerFactory.getLogger(args.getString(0));


        switch (index) {
            case 0:
                logger.info(args.getString(1));
                break;
            case 1:
                logger.warn(args.getString(1));
                break;
            case 2:
                logger.error(args.getString(1));
                break;
        }
        return Variable.VOID();
    }
}
