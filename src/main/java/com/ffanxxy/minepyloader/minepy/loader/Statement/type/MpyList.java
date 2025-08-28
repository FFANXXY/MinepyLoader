package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MpyList {
    private final List<Variable<?>> list;
    private final DataType dataType;

    public MpyList(DataType dataType, List<Variable<?>> list) {
        this.list = new ArrayList<>(
                list.stream().filter(
                        variable -> variable.isSameDataType(dataType)
                ).toList()
        );
        this.dataType = dataType;
    }


    public MpyList(DataType dataType, List<VarGetterNode> varGetterNodes , Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        List<Variable<?>> variables = new ArrayList<>();
        for (VarGetterNode varGetterNode : varGetterNodes) {
            Variable<?> variable = varGetterNode.runWithArg(variableMap);
            variables.add(variable);
        }
        this.list = new ArrayList<>(
                variables.stream().filter(
                        variable -> variable.isSameDataType(dataType)
                ).toList()
        );
        this.dataType = dataType;
    }

    public Variable<?> get(int index) {
        return list.get(index);
    }

    public @NotNull Variable<?> set(int index, Variable<?> variable) {
        if(variable.isSameDataType(this.dataType)) {
            list.set(index, variable);
            return Variable.VOID();
        } else {
            throw new RuntimeException("The input variable is not same type as the list. Need " + this.dataType.getChild().getName()
                    + " in fact" + variable.getDataType().getName());
        }
    }

    public @NotNull Variable<?> add(int index , Variable<?> variable) {
        if(variable.isSameDataType(this.dataType)) {
            list.add(index, variable);
            return Variable.VOID();
        } else {
            throw new RuntimeException("The input variable is not same type as the list. Need " + this.dataType.getChild().getName()
                    + " in fact" + variable.getDataType().getName());
        }
    }

    public @NotNull Variable<?> add(Variable<?> variable) {
        if(variable.isSameDataType(this.dataType)) {
            list.add(variable);
            return Variable.VOID();
        } else {
            throw new RuntimeException("The input variable is not same type as the list. Need " + this.dataType.getChild().getName()
                    + " in fact" + variable.getDataType().getName());
        }
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public @NotNull Variable<Integer> size() {
        return Variable.ofInteger("%TEMP", list.size());
    }
}
