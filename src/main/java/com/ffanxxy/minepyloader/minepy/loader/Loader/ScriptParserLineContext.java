package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.List;
import java.util.Map;

public record ScriptParserLineContext(
        String line,
        Map<String, DataType> defineVarContext,
        List<String> imports,
        PackageStructure structure
) {
    public static ScriptParserLineContext createWithNewLine(ScriptParserLineContext context, String newLine) {
        return new ScriptParserLineContext(newLine, context.defineVarContext, context.imports,context.structure);
    }
}
