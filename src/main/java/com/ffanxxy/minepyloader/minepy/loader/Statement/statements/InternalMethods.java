package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.*;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalMethods {

    @FunctionalInterface
    public interface MethodsNodeFactory {
        MethodsNode apply(List<VarGetterNode> list, ScriptParserLineContext context, String methodName);
    }

    public static Map<String, MethodsNodeFactory> internalMethods = new HashMap<>();

    static {
        register("mpy.Logger",LoggerNode::new);
        register("mpy.Player",PlayerNode::new);
        register("mpy.World",WorldNode::new);
        register("mpy.String",StringNode::new);
        register("mpy.List",ListNode::new);
        register("mpy.Text",TextNode::new);
        register("mpy.Style",StyleNode::new);
    }

    public static void register(String name, MethodsNodeFactory factory) {
        internalMethods.put(name, factory);
    }

    public static MethodsNode get(String method, List<VarGetterNode> parameters, ScriptParserLineContext context) {

        PackageStructure packageStructure = PackageStructure.create(method);

        for(String name : internalMethods.keySet()) {
            if (packageStructure.subList(2).isSameAs(PackageStructure.create(name))) {
                return internalMethods.get(name).apply(parameters, context, packageStructure.get(2));
            }
        }
        throw new RuntimeException("Unknow method: " + method);
    }

    public static boolean contains(String method) {
        PackageStructure s = new PackageStructure(method);
        if(!s.getFirst().equals("mpy")) {
            s.addFirst("mpy");
        }
        String c = s.toString();

        for(String keys : internalMethods.keySet()) {
            if(keys.contains(c)) return true;
        }
        return false;
    }
}
