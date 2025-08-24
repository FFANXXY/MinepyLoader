package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.RunnableBlock;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IfNode extends ControlNode {
    public IfNode(String condition, ScriptParserLineContext context) {
        super(condition, context);
    }

    @Override
    public boolean getRepeating() {
        return false;
    }
}
