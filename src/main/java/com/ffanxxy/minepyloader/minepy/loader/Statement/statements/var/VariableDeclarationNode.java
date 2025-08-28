package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Map;

public class VariableDeclarationNode implements RunnableNode {

    private final DataType dataType;
    private final String varName;
    private final VarGetterNode node;

    public DataType getDataType() {
        return this.dataType;
    }

    public String getVarName() {
        return this.varName;
    }

    public VariableDeclarationNode(DataType dataType, String varName) {
        this.dataType = dataType;
        this.varName = varName;
        this.node = null;
    }

    public VariableDeclarationNode(DataType dataType, String varName, VarGetterNode value) {
        this.dataType = dataType;
        this.varName = varName;
        this.node = value;
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        Variable<?> variable;

        if(node != null) {
            variable = Variable.create(this.varName, node.runWithArg(variableMap));
            // Double转Float
            // 对象转换处理
            if ((variable.isSameDataType(DataType.DOUBLE) || variable.isSameDataType(DataType.LITERAL_DOUBLE)) &&
                    dataType == DataType.FLOAT) {
                variable = variable.toFloat();
            }

        } else {
            variable = Variable.create(this.varName, dataType);
        }

        variableMap.put(new Minepy.ScopeAndName(1, variable.getName()), variable);
        return Variable.VOID();
    }
}
