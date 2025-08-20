package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

public class Argument {

    private final Variable<?> variable;

    public Argument(Variable<?> variable) {
        this.variable = variable;
    }

    public Argument(String name, Variable<?> variable) {
        this.variable = Variable.createWithNewName(name, variable);
    }

    public Variable<?> getVariable() {
        return variable;
    }

    public static Argument create(Variable<?> variable) {
        return new Argument(variable);
    }

}
