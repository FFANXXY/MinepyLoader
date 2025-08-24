package com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.BreakNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ControlNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.IfNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ReturnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statements implements RunnableBlock {
    List<RunnableBlock> blocks = new ArrayList<>();

    public Statements() {

    }

    public void join(RunnableBlock block) {
        blocks.add(block);
    }

    public void join(Statements statements) {
        blocks.addAll(statements.blocks);
    }


    // 专用
    public ControlReturnResult runInControl(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {

        for (RunnableBlock block : blocks) {
           var crr =  block.runInControl(variableMap);
            if (block instanceof Statement statement) {
                if (statement.node instanceof ReturnNode) return new ControlReturnResult(true, crr.variable());
                if (statement.node instanceof BreakNode) return new ControlReturnResult(false, null);
                if (statement.node instanceof ControlNode node) {
                    // 控制节点，获得回调
                    if(node.shouldCallBack()) {
                        return new ControlReturnResult(true, crr.variable());
                    }

                    if(node instanceof IfNode && crr.variable() == null) {
                        return new ControlReturnResult(false, null);
                    }
                }
            }
        }
        return new ControlReturnResult(false, Variable.VOID());
    }

    @Override
    public Variable<?> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        Variable<?> result;
        for (RunnableBlock block : blocks) {
            result = block.run(variableMap);

            // 退出判断
            if (block instanceof Statement statement) {
                if (statement.node instanceof ReturnNode) return result;
                if(statement.node instanceof ControlNode node) {
                    // 控制节点，获得回调
                    if(node.shouldCallBack()) {
                        return result;
                    }
                }
            }
        }
        return Variable.VOID();
    }
}
