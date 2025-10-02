package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.MethodModifier;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierInputContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context.ModifierOutput;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 方法的存储类，用于查找
 *
 * @see Parameter
 * @see DataType
 */
public final class Method {

    private final ScriptPackage path;

    private final List<MethodModifier> modifiers;
    private final String name;
    private final DataType type;
    private final List<Parameter> parameters;
    private final List<Parameter> factParameters;

    private final Statements statements = new Statements();

    /**
     * @param path       存放目录，用于获得路径
     * @param name       方法名称
     * @param type       返回值类型
     * @param parameters 参数列表
     */
    public Method(
            ScriptPackage path,
            List<MethodModifier> modifiers,
            String name,
            DataType type,
            List<Parameter> parameters,
            List<Parameter> factParameters
    ) {
        this.path = path;
        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.factParameters = factParameters;
    }

    public void addStatements(Statements statements) {
        this.statements.join(statements);
    }

    public void addStatement(Statement statement) {
        this.statements.join(statement);
    }

    public void runStatic() {
        if (parameters.isEmpty() && modifiers.contains(MethodModifiers.LOAD)) {
            this.statements.run(new HashMap<>());
        }
    }

    public CompletableFuture<Variable<?>> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap, MethodExecutor executor) {

        // 执行修饰符逻辑
        for(MethodModifier modifier : this.modifiers) {

            ModifierOutput output = modifier.modify(new ModifierInputContext(executor, PackageStructure.create(path)));

            if(output == null ) continue;

            if(output.skip()) {
                return CompletableFuture.completedFuture(Variable.VOID());
            }
        }

        return CompletableFuture.supplyAsync(
                () -> {
                    // 获得运行结果
                    Variable<?> returnValue = statements.run(variableMap);
                    // 类型判断
                    if (returnValue.getDataType().isSameTypeAs(this.type)) {
                        return returnValue;
                    } else {

                        // 模拟继承关系
                        if (this.type == DataType.OBJECT) {
                            return returnValue;
                        } else if ((returnValue.isSameDataType(DataType.FLOAT) || returnValue.isSameDataType(DataType.INT)) && this.type == DataType.DOUBLE) {
                            return Variable.ofDouble("%TEMP", Double.parseDouble(returnValue.toString()));
                        } else if (returnValue.isSameDataType(DataType.INT) && this.type == DataType.FLOAT) {
                            return Variable.ofFloat("%TEMP", Float.parseFloat(returnValue.toString()));
                        }

                        throw new RuntimeException("Return Value is not same as its define type: " + MethodHelper.getMethodFullName(this) +
                                " needs " + this.type + " ,in fact: " + returnValue.getDataType());
                    }
                }
        );
    }

    public ScriptPackage getPath() {
        return path;
    }

    public MethodModifier getAccessModifiers() {
        if(this.modifiers.get(0).getType().isAccessModifier()) {
            return this.modifiers.get(0);
        } else {
            return MethodModifiers.DEFAULT;
        }
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<MethodModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Method) obj;
        return that.path.isSamePackage(((Method) obj).path.toString());
    }

    public List<Parameter> getFactParameters() {
        return factParameters;
    }
}
