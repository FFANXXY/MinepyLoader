package com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;

import java.util.Map;

public interface RunnableBlock {
    Variable<?> run(Map<Minepy.ScopeAndName, Variable<?>> variableMap);
}
