package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ModifierOutput {
    private final boolean skip;
    private final List<DataType> needParas;

    public ModifierOutput(
            boolean skip,
            List<DataType> needParas
    ) {
        this.skip = skip;
        this.needParas = needParas;
    }

    public ModifierOutput(
            boolean skip
    ) {
        this.skip = skip;
        this.needParas = new ArrayList<>();
    }

    public boolean skip() {
        return skip;
    }

    public List<DataType> needParas() {
        return needParas;
    }

}
