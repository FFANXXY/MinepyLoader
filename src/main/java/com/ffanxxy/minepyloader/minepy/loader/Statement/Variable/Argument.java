package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

public class Argument {

    private final Variable<?> variable;

    public Argument(Variable variable) {
        this.variable = variable;
    }

    public Argument(int index , String str) {
        this.variable = Variable.ofString("%TEMP" + index, str);
    }

    public Variable<?> getVariable() {
        return variable;
    }


}
