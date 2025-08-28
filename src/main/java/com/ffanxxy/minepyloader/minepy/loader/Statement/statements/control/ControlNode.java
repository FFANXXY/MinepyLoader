package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.control;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.RunnableBlock;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;

import java.util.Map;

public abstract class ControlNode implements RunnableNode {
    protected final VarGetterNode node;

    protected Statements statements = new Statements();
    protected boolean callback = false;

    protected int cyclingCount = 0;

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

    public void beforeRunning(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {}
    public void afterCycling(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {}
    public void beforeReturn(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {}

    public boolean shouldCallBack() {
        return this.callback;
    }


    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {

        // 使用其它线程完成循环的操作
        // 否则会导致堵塞主线程引发各种异常
        //                  -- Fansyri

        beforeRunning(variableMap);

        if(getRepeating()) {
            while (node.runWithArg(variableMap).getAsBoolean().getValue()) {
                cyclingCount++;
                RunnableBlock.ControlReturnResult crr = statements.runInControl(variableMap);

                if(crr.CallBack()) {
                    this.callback = true;

                    beforeReturn(variableMap);
                    return crr.variable();
                } else if(crr.variable() == null) {
                    // 说明是break
                    beforeReturn(variableMap);
                    return null;
                }
                afterCycling(variableMap);
            }
        } else {
            if(node.runWithArg(variableMap).getAsBoolean().getValue()) {
                RunnableBlock.ControlReturnResult crr = statements.runInControl(variableMap);

                if(crr.CallBack()) {
                    this.callback = true;
                    beforeReturn(variableMap);
                    return crr.variable();
                } else if(crr.variable() == null) {
                    // 说明是break
                    beforeReturn(variableMap);
                    return null;
                }
            }
        }
        beforeReturn(variableMap);
        return Variable.VOID();
    }
}
