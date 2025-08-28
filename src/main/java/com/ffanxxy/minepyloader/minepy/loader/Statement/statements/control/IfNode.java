package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.control;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;

public class IfNode extends ControlNode {
    public IfNode(String condition, ScriptParserLineContext context) {
        super(condition, context);
    }

    @Override
    public boolean getRepeating() {
        return false;
    }
}
