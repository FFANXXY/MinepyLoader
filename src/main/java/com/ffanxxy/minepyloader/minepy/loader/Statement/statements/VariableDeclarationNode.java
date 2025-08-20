package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Map;

public class VariableDeclarationNode implements RunnableNode {

    private final DataType dataType;
    private final String name;
    private final VarGetterNode node;

    public DataType getDataType() {
        return this.dataType;
    }

    public String getName() {
        return this.name;
    }

    public VariableDeclarationNode(DataType dataType, String name) {
        this.dataType = dataType;
        this.name = name;
        this.node = null;
    }

    public VariableDeclarationNode(DataType dataType, String name, VarGetterNode value) {
        this.dataType = dataType;
        this.name = name;
        this.node = value;
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        Variable<?> variable = Variable.createWithNewName(this.name, node.runWithArg(variableMap));

        // Double转Float
        // 对象转换处理
        if ((variable.isDataType(DataType.DOUBLE) || variable.isDataType(DataType.LITERAL_DOUBLE)) &&
                dataType == DataType.FLOAT) {
            variable = variable.toFloat();
        }

        variableMap.put(new Minepy.ScopeAndName(1, variable.getName()), variable);
        return Variable.VOID();
    }
}
