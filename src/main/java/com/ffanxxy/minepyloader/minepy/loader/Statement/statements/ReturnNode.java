package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;

import java.util.Map;

public class ReturnNode implements RunnableNode{

    // 一定为一个赋值节点
    public VarGetterNode node;

    public ReturnNode(VarGetterNode getterNode) {
        node = getterNode;
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        return node.runWithArg(variableMap);
    }
}
