package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListNode extends MethodsNode {
    public ListNode(List<VarGetterNode> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("get", 0, DataType.LIST, DataType.INT);
        builder.add("set", 1, DataType.LIST, DataType.INT, DataType.OBJECT);
        builder.add("add", 2, DataType.LIST, DataType.INT, DataType.OBJECT);
        builder.add("add", 3, DataType.LIST, DataType.OBJECT);
        builder.add("size", 4, DataType.LIST);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0 -> args.getList(0).get(args.getInteger(1));
            case 1 -> args.getList(0).set(args.getInteger(1), args.getVariable(2));
            case 2 -> args.getList(0).add(args.getInteger(1), args.getVariable(2));
            case 3 -> args.getList(0).add(args.getVariable(1));
            case 4 -> args.getList(0).size();
            default -> Variable.VOID();
        };
    }
}
