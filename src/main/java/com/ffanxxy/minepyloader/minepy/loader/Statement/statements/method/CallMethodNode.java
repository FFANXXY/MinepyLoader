package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Method;
import com.ffanxxy.minepyloader.minepy.loader.Loader.MethodExecutor;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;
import com.ffanxxy.minepyloader.minepy.utils.loader.PackageGetter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 方法语句调用存储
 */
public class CallMethodNode implements RunnableNode {
    private PackageStructure methodPackage;
    private final List<VarGetterNode> Vars;

    private final PackageStructure executor;

    public CallMethodNode(String methodName, List<VarGetterNode> Vars, ScriptParserLineContext context) {
        List<String> imports = context.imports();

        methodPackage = PackageGetter.getNameInImport(methodName,imports);

        this.Vars = Vars;
        this.executor = context.structure();
    }


    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        // 获得参数
        List<Variable<?>> variables = new ArrayList<>();
        Vars.forEach(
                varGetterNode -> variables.add(varGetterNode.runWithArg(variableMap))
        );

        // 获得方法
        Method mtd = MethodHelper.getMethodFromVars(this.methodPackage.toString(), variables);

        // 获得形参
        List<Parameter> parameters = mtd.getParameters();

        if (parameters.size() != variables.size())
            throw new RuntimeException("The number of parameters is wrong: " + methodPackage);
        if (parameters.isEmpty()) {
            try {
                var future = mtd.run(new HashMap<>(), new MethodExecutor(MethodExecutor.ExecutorType.STATEMENT, executor.toString()));
                return future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Minepy.ScopeAndName, Variable<?>> resultRunArgs = new HashMap<>();

        // 对输入的参数进行解析
        for (int i = 0; i < variables.size(); i++) {
            var methodParameter = parameters.get(i);
            var inputVariable = variables.get(i);
            resultRunArgs.put(
                    new Minepy.ScopeAndName(0, methodParameter.name),
                    inputVariable
            );
        }

        try {
            var future = mtd.run(resultRunArgs,  new MethodExecutor(MethodExecutor.ExecutorType.STATEMENT, executor.toString()));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
