package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;

import java.util.List;

public class InternalMethods {
    public static RunnableNode get(String method, List<Parameter> parameters) {
        return switch (method) {
            case "mpy.log" -> new LoggerNode.log(parameters);
            case "mpy.warn" -> new LoggerNode.warn(parameters);
            case "mpy.err" , "mpy.error" -> new LoggerNode.err(parameters);
            default -> throw new RuntimeException("Unknow method: " + method);
        };
    }
}
