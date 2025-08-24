package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;

import java.util.List;
import java.util.Map;

public interface RunnableNode {
    Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap);
}
