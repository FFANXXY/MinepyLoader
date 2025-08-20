package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 方法语句调用存储
 */
public class CallMethodNode implements RunnableNode{
    private final String method;
    private final List<Parameter> InParameters;

    public CallMethodNode(String method, List<Parameter> InParameters, Map<String, DataType> defineContext) {
        this.method = method;
        this.InParameters = InParameters;
        // 处理参数
        for (Parameter p : InParameters) {
            if (p.dataType == DataType.VAR) {
                // 从定义上下文获得变量类型
                p.dataType = defineContext.get(p.name);
            }
        }
    }


    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        // 获得方法
        Minepy.Method mtd = MethodHelper.getMethod(this.method, this.InParameters);

        // 获得形参
        List<Parameter> parameters = mtd.parameters();

        if (parameters.size() != InParameters.size())
            throw new RuntimeException("The number of parameters is wrong: " + method);
        if (parameters.isEmpty()) return mtd.run(new ArrayList<>());

        List<Argument> resultRunArgs = new ArrayList<>();

        // 对输入的参数进行解析
        for (int i = 0; i < InParameters.size(); i++) {
            var methodParameter = parameters.get(i);
            var inParameter = InParameters.get(i);

            if (!methodParameter.dataType.isSameTypeAs(inParameter.dataType))
                throw new RuntimeException("The required parameter types are not paired: when-" + method);

            if (inParameter.dataType.isLiteral()) {
                Variable<?> variable;

                switch (inParameter.dataType) {
                    case LITERAL_STRING -> variable = Variable.ofString(inParameter.name, inParameter.name);
                    case LITERAL_INTEGER ->
                            variable = Variable.ofInteger(inParameter.name, Integer.parseInt(inParameter.name));
                    default -> variable = Variable.NULL();
                }

                resultRunArgs.add(
                        new Argument(methodParameter.name, variable)
                );
            } else {
                Variable<?> variable = Minepy.getFromSAN(inParameter.name, variableMap);
                if (variable == null) throw new RuntimeException("Unknown Variable: " + inParameter.name);
                resultRunArgs.add(
                        new Argument(methodParameter.name, variable)
                );
            }
        }

        return mtd.run(resultRunArgs);
    }
}
