package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Method;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法语句调用存储
 */
public class CallMethodNode implements RunnableNode {
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
        Method mtd = MethodHelper.getMethod(this.method, this.InParameters);

        // 获得形参
        List<Parameter> parameters = mtd.getParameters();

        if (parameters.size() != InParameters.size())
            throw new RuntimeException("The number of parameters is wrong: " + method);
        if (parameters.isEmpty()) return mtd.run(new HashMap<>());

        Map<Minepy.ScopeAndName, Variable<?>> resultRunArgs = new HashMap<>();

        // 对输入的参数进行解析
        for (int i = 0; i < InParameters.size(); i++) {
            var methodParameter = parameters.get(i);
            var inParameter = InParameters.get(i);

            if (!methodParameter.dataType.isSameTypeAs(inParameter.dataType))
                throw new RuntimeException("The required parameter types are not paired: when-" + method);

            if (inParameter.dataType.isLiteral()) {
                Variable<?> variable = getVariable(inParameter);

                resultRunArgs.put(
                        new Minepy.ScopeAndName(0, methodParameter.name),
                        variable
                );
            } else {
                Variable<?> variable = Minepy.getFromSAN(inParameter.name, variableMap);
                if (variable == null) throw new RuntimeException("Unknown Variable: " + inParameter.name);
                resultRunArgs.put(
                        new Minepy.ScopeAndName(0, methodParameter.name),
                        variable
                );
            }
        }

        return mtd.run(resultRunArgs);
    }

    private static @NotNull Variable<?> getVariable(Parameter inParameter) {
        Variable<?> variable;

        switch (inParameter.dataType) {
            case LITERAL_STRING -> variable = Variable.ofString(inParameter.name, inParameter.name);
            case LITERAL_INTEGER ->
                    variable = Variable.ofInteger(inParameter.name, Integer.parseInt(inParameter.name));
            case LITERAL_BOOLEAN -> variable = Variable.ofBoolean(inParameter.name, Boolean.parseBoolean(inParameter.name));
            case LITERAL_CHAR -> variable = Variable.ofChar(inParameter.name, inParameter.name.charAt(0));
            case LITERAL_FLOAT -> variable = Variable.ofFloat(inParameter.name, Float.parseFloat(inParameter.name));
            case LITERAL_DOUBLE -> variable = Variable.ofDouble(inParameter.name, Double.parseDouble(inParameter.name));
            case LITERAL_NULL -> variable = Variable.NULL();
            default -> throw new RuntimeException("Unknown Exception: Because literal var");
        }
        return variable;
    }
}
