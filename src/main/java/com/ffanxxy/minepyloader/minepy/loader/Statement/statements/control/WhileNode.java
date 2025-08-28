package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.control;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;

public class WhileNode extends ControlNode{
    public WhileNode(String cod, ScriptParserLineContext context) {
        super(cod, context);
    }

    @Override
    public boolean getRepeating() {
        return true;
    }
}
