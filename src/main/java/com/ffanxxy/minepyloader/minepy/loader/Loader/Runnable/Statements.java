package com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ReturnNode;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * 对于语句组里的所有语句运行，运行初始化参数
     *
     * @param arguments 实参
     * @return 返回值
     */
    public Variable<?> runWithArg(List<Argument> arguments) {
        Map<Minepy.ScopeAndName, Variable<?>> variableMap = new HashMap<>();
        arguments.forEach(
                argument -> variableMap.put(
                        new Minepy.ScopeAndName(0, argument.getVariable().getName()),
                        argument.getVariable()
                )
        );

        Variable<?> result;

        for (RunnableBlock block : blocks) {
            result = block.run(variableMap);
            if (block instanceof Statement statement) {
                if (statement.node instanceof ReturnNode) return result;
            }
        }
        return Variable.VOID();
    }

    @Override
    public Variable<?> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        Variable<?> result;
        for (RunnableBlock block : blocks) {
            result = block.run(variableMap);
            if (block instanceof Statement statement) {
                if (statement.node instanceof ReturnNode) return result;
            }
        }
        return Variable.VOID();
    }
}
