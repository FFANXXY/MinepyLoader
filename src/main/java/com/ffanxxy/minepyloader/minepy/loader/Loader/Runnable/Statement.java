package com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.BreakNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ReturnNode;
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

    @Override
    public ControlReturnResult runInControl(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        if (node instanceof ReturnNode) {
            return new ControlReturnResult(true, node.runWithArg(variableMap));
        } else if (node instanceof BreakNode) {
            return new ControlReturnResult(false, node.runWithArg(variableMap));
        } else {
            return new ControlReturnResult(false, node.runWithArg(variableMap));
        }
    }
}
