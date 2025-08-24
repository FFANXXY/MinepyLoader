package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.RunnableBlock;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;

import java.util.Map;

public abstract class ControlNode implements RunnableNode{
    protected final VarGetterNode node;

    protected Statements statements = new Statements();
    protected boolean callback = false;

    public ControlNode(String cod, ScriptParserLineContext context) {
        this.node = StatementManager.getVarGetterNode(cod, context);
    }

    public void addStatement(RunnableBlock node) {
        statements.join(node);
    }

    public void addStatement(RunnableNode node) {
        statements.join(new Statement(node));
    }

    public void addStatements(Statements statements) {
        statements.join(statements);
    }

    public abstract boolean getRepeating();

    public boolean shouldCallBack() {
        return this.callback;
    }


    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {

        if(getRepeating()) {
            while (node.runWithArg(variableMap).getAsBoolean().getValue()) {
                RunnableBlock.ControlReturnResult crr = statements.runInControl(variableMap);

                if(crr.CallBack()) {
                    this.callback = true;
                    return crr.variable();
                } else if(crr.variable() == null) {
                    // 说明是break
                    return null;
                }
            }
        } else {
            if(node.runWithArg(variableMap).getAsBoolean().getValue()) {
                RunnableBlock.ControlReturnResult crr = statements.runInControl(variableMap);

                if(crr.CallBack()) {
                    this.callback = true;
                    return crr.variable();
                } else if(crr.variable() == null) {
                    // 说明是break
                    return null;
                }
            }
        }
        return Variable.VOID();
    }
}
