package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.Parser.MethodParser;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ControlNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VariableDeclarationNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.loader.scriptObject.Script;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedStatementException;

import java.util.*;

public class Minepy {

    public static List<Method> METHODS = new ArrayList<>();

    /**
     * 方法定义类
     * @param name 方法名称
     * @param type 返回值类型
     * @param parameters 参数列表
     */
    public record MethodDefiner(
            AccessModifiers accessModifiers,
            List<MethodModifiers> modifiers,
            String name,
            DataType type,
            List<Parameter> parameters
    ) {
      public Method toMethod(ScriptPackage path) {
          return new Method(
                  path,
                  accessModifiers,
                  modifiers,
                  name,
                  type,
                  parameters
          );
      }
    };

    /*
    Minepy主体
     */

    private final Script script;

    private ScriptPackage path = null;
    private List<String> imports = new ArrayList<>();

    public Minepy(Script script) {

        this.script = script;

        Statements alls = new Statements();

        List<Line> readLines = new ArrayList<>();
        for (String line : script.getLines()) {
            int space = getSpace(line);
            readLines.add(new Line(space, line.trim()));
        }

        boolean isInMethod = false;
        Method DemoMethod = null;

        // 获得定义上下文
        Map<String, DataType> defineVarContext = new HashMap<>();

        List<ControlNode> controlNodesPlans = new ArrayList<>();

        // 变为Method
        for(Line line : readLines) {
            // 注释的优先级最高
            if(line.line.startsWith("//")) continue;

            if(line.line.startsWith("#")) {
                // 获得头声明
                String[] kv = line.line.substring(1).split("\\s+");

                switch (kv[0]) {
                    case "package":
                        if(path != null) return;
                        path = new ScriptPackage(kv[1]);
                        break;
                    case "import":
                        imports.add(kv[1]);
                        break;
                    default:
                        break;
                }

                continue;
            }

            // 行为空，则继续
            if(line.line.isEmpty()) continue;

            // 如果没有缩进，则视为您一个方法
            if( ( line.retraction == 0 ) && isInMethod) {
                isInMethod = false;
                METHODS.add(DemoMethod);
                DemoMethod = null;
                defineVarContext = new HashMap<>();
            }

            // 若没有缩进，则视为方法
            if(line.retraction == 0) {
                MethodParser methodParser = new MethodParser(line.line);
                    DemoMethod = methodParser.method.toMethod(path);
                    isInMethod = true;
                    // 创建定义上下文
                    for(Parameter p : methodParser.getParameterParser().getParameters()) {
                        defineVarContext.put(p.name, p.dataType);
                    }
            } else {

                int retraction = line.retraction;
                // 向上取整为层数
                int level = retraction / 4 + (retraction %4 == 0 ? 0 : 1);
                // 应该处于level1

                // 方法体内容判断
                if(!isInMethod) throw new UnexpectedStatementException("atPath: " + script.getPath().toString() + "  ;atLine" + line.retraction);

                // 构建上下文
                ScriptParserLineContext ctx = new ScriptParserLineContext(
                        line.line,
                        defineVarContext,
                        imports
                );

                StatementManager manager = new StatementManager(ctx);

                /*
                 * 监听语句是否为变量定义，并添加定义上下文
                 * 可提取到类，作为事件处理
                 */
                if(manager.getCodeType() == StatementManager.CodeType.VARIABLE_DECLARATION) {
                    if(manager.get() instanceof VariableDeclarationNode node) {
                        defineVarContext.put(node.getName(), node.getDataType());
                    }
                } else if(manager.getCodeType() == StatementManager.CodeType.CONTROL_STATEMENT) {
                    controlNodesPlans.add((ControlNode) manager.get());
                }

                // when it has no plans , use DEFAULT ADD
                // MUSTN't ADD ( ... || manager.getCodeType() == StatementManager.CodeType.CONTROL_STATEMENT) There,
                // Or the inside control statement won't be added inside
                if(controlNodesPlans.isEmpty()) {
                    DemoMethod.addStatement(new Statement(manager.get()));
                    continue;
                }

                if(level == 1 && manager.getCodeType() == StatementManager.CodeType.CONTROL_STATEMENT) {
                    // 为了防止方法中底层添加时出现问题，检测到为控制语句时执行一般返回
                     DemoMethod.addStatement(new Statement(manager.get()));
                     continue;
                }

                ControlNode controlNode = controlNodesPlans.get(controlNodesPlans.size() - 1);

                // 为当前层数
                if(controlNodesPlans.size() == level - 1) {
                    controlNode.addStatement(manager.get());
                    continue;
                } else if(controlNodesPlans.size() == level && manager.getCodeType() == StatementManager.CodeType.CONTROL_STATEMENT) {
                    // 如果等于，并且是控制语句，则认为是嵌套
                    controlNodesPlans.get(controlNodesPlans.size() - 2).addStatement(manager.get());
                    continue;
                } else if(level != 1) {
                    for (int i = 0; i < level - 1; i++) {
                        controlNodesPlans.remove(controlNodesPlans.size() - 1);
                    }
                    //  重新获得节点
                    controlNode = controlNodesPlans.get(controlNodesPlans.size() - 1);
                    controlNode.addStatement(manager.get());
                    continue;
                }

                // 运行到这，说明plans不为空，而level == 1

                controlNodesPlans = new ArrayList<>();
                DemoMethod.addStatement(new Statement(manager.get()));

            }
        }
        if(DemoMethod != null) {
            METHODS.add(DemoMethod);
        }
    }

    public ScriptPackage getPackage() {
        return path;
    }

    public static int getSpace(String str) {
        return str.replaceAll("( *).*", "$1").length();
    }

    /**
     * 在开始Loader初始化时执行
     * @see Method#runStatic()
     * @see Minepyloader#onInitialize()
     */
    public void runStatic() {
        METHODS.stream().filter(
                method -> method.getPath().isPack(this.path) && method.getModifiers().contains(MethodModifiers.LOAD)
        ).forEach(Method::runStatic);
    }


    public record ScopeAndName(Integer integer, String name) {};

    public static Variable<?> getFromSAN(String name, Map<ScopeAndName,Variable<?>> map) {
        for(ScopeAndName scopeAndName : map.keySet()) {
            if(scopeAndName.name.equals(name)) {
                return map.get(scopeAndName);
            }
        }
        return Variable.VOID();
    }

    record Line(int retraction, String line) {}
}
