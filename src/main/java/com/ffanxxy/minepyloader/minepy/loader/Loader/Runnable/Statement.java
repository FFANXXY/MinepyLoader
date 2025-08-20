package com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;

import java.util.Map;

public class Statement implements RunnableBlock {

    public RunnableNode node;

    public Statement(RunnableNode node) {
        this.node = node;
    }

    @Override
    public Variable<?> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        return node.runWithArg(variableMap);
    }
}
