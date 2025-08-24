package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.LoggerNode;

import java.util.List;

public class InternalMethods {
    public static RunnableNode get(String method, List<Parameter> parameters, ScriptParserLineContext context) {

        PackageStructure packageStructure = PackageStructure.create(method);

        return switch (packageStructure.get(1)) {
            case "Logger" -> new LoggerNode(parameters, context, packageStructure.get(2));
            case "World" -> new WorldNode(parameters, context, packageStructure.get(2));
            case "Player" -> new PlayerNode(parameters, context, packageStructure.get(2));
            case "String" -> new StringNode(parameters, context, packageStructure.get(2));
            default -> throw new RuntimeException("Unknow method: " + method);
        };
    }

    public static boolean contains(String method) {
        PackageStructure s = new PackageStructure(method);
        if(!s.getFirst().equals("mpy")) {
            s.addFirst("mpy");
        }
        String c = s.toString();

        return switch (c) {
            case "mpy.Logger" -> true;
            case "mpy.World" ->true;
            case "mpy.Player" -> true;
            case "mpy.String" -> true;
            default -> false;
        };
    }
}
