package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 方法的存储类，用于查找
 *
 * @see Parameter
 * @see DataType
 */
public final class Method {

    private final ScriptPackage path;
    private final AccessModifiers accessModifiers;
    private final List<MethodModifiers> modifiers;
    private final String name;
    private final DataType type;
    private final List<Parameter> parameters;

    private final Statements statements = new Statements();

    /**
     * @param path       存放目录，用于获得路径
     * @param name       方法名称
     * @param type       返回值类型
     * @param parameters 参数列表
     */
    public Method(
            ScriptPackage path,
            AccessModifiers accessModifiers,
            List<MethodModifiers> modifiers,
            String name,
            DataType type,
            List<Parameter> parameters
    ) {
        this.path = path;
        this.accessModifiers = accessModifiers;
        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
        this.parameters = parameters;
    }

    public void addStatements(Statements statements) {
        this.statements.join(statements);
    }

    public void addStatement(Statement statement) {
        this.statements.join(statement);
    }

    public void runStatic() {
        if (parameters.isEmpty()) {
            this.statements.run(new HashMap<>());
        }
    }

    public Variable<?> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        // 获得运行结果
        Variable<?> backvar = statements.run(variableMap);
        // 类型判断
        if (backvar.getDataType().isSameTypeAs(this.type)) {
            return backvar;
        } else {

            // 模拟继承关系
            if (this.type == DataType.OBJECT) {
                return backvar;
            } else if ((backvar.isSameDataType(DataType.FLOAT) || backvar.isSameDataType(DataType.INT)) && this.type == DataType.DOUBLE) {
                return Variable.ofDouble("%TEMP", Double.parseDouble(backvar.toString()));
            } else if (backvar.isSameDataType(DataType.INT) && this.type == DataType.FLOAT) {
                return Variable.ofFloat("%TEMP", Float.parseFloat(backvar.toString()));
            }

            throw new RuntimeException("Return Value is not same as its define type: " + MethodHelper.getMethodFullName(this) +
                    " needs " + this.type + " ,in fact: " + backvar.getDataType());
        }
    }

    public ScriptPackage getPath() {
        return path;
    }

    public AccessModifiers getAccessModifiers() {
        return accessModifiers;
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

    public List<MethodModifiers> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Method) obj;
        return Objects.equals(this.path, that.path) &&
                Objects.equals(this.accessModifiers, that.accessModifiers) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, accessModifiers, name, type, parameters);
    }

    @Override
    public String toString() {
        return "Method[" +
                "path=" + path + ", " +
                "accessModifiers=" + accessModifiers + ", " +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "parameters=" + parameters + ']';
    }

}
