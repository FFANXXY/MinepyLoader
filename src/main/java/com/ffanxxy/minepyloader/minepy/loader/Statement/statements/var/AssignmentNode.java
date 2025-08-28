package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Map;

public class AssignmentNode implements RunnableNode {

    private String name;
    private VarGetterNode varGetterNode;


    // 赋值
    public AssignmentNode(String objectName, VarGetterNode varGetterNode) {
        this.name = objectName;
        this.varGetterNode = varGetterNode;
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {

        Variable<?> varNow = Variable.create(this.name, varGetterNode.runWithArg(variableMap));
        Variable<?> vLast = Minepy.getFromSAN(name, variableMap);

        // Double转Float
        // 对象转换处理
        if ((varNow.isSameDataType(DataType.DOUBLE) || varNow.isSameDataType(DataType.LITERAL_DOUBLE)) &&
                vLast.getDataType() == DataType.FLOAT) {
            varNow = varNow.toFloat();
        }

        // 判断类型
        if(!varNow.isSameDataType(vLast)) throw new RuntimeException("The data type is not same: " + name
        + " needs " + vLast.getDataType().getName() + " but in fact " + varNow.getDataType().getName());

        vLast.setValue(varNow.getValue());

        return Variable.VOID();
    }
}
