package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;

public class ValueGetter {
    public static VarGetterNode getWhenReading(String var, ScriptParserLineContext context) {
        return StatementManager.getVarGetterNode(var, context);
    }
}
