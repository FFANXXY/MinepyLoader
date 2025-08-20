package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statement;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Runnable.Statements;
import com.ffanxxy.minepyloader.minepy.loader.Parser.MethodParser;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.VariableDeclarationNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.loader.scriptObject.Script;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedStatementException;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;

import java.util.*;

public class Minepy {

    public static List<Method> METHODS = new ArrayList<>();

    /**
     * 方法的存储类，用于查找
     *
     * @see Parameter
     * @see DataType
     */
        public static final class Method {

        private final ScriptPackage path;
        private final AccessModifiers accessModifiers;
        private final List<MethodModifiers> modifiers;
        private final String name;
        private final DataType type;
        private final List<Parameter> parameters;

        private final Statements statements = new Statements();

        /**
         * @param path       存放目录，用于获得路径
         * @param name       方法名称
         * @param type       返回值类型
         * @param parameters 参数列表
         */
        public Method(
                ScriptPackage path,
                AccessModifiers accessModifiers,
                List<MethodModifiers> modifiers,
                String name,
                DataType type,
                List<Parameter> parameters
        ) {
            this.path = path;
            this.accessModifiers = accessModifiers;
            this.modifiers = modifiers;
            this.name = name;
            this.type = type;
            this.parameters = parameters;
        }

        public void addStatements(Statements statements) {
            this.statements.join(statements);
        }
        public void addStatement(Statement statement) {
            this.statements.join(statement);
        }

        public void runStatic() {
            if(parameters.isEmpty()) {
                this.statements.runWithArg(new ArrayList<>());
            }
        }

        public Variable<?> run(List<Argument> arguments) {
            // 获得运行结果
            Variable<?> backvar = statements.runWithArg(arguments);
            // 类型判断
            if(backvar.getDataType().isSameTypeAs(this.type)) {
                return backvar;
            } else {
                throw new RuntimeException("Return Value is not same as its define type: " + MethodHelper.getMethodFullName(this) +
                        " needs " + this.type + " ,in fact: " + backvar.getDataType());
            }
        }

        public ScriptPackage path() {
            return path;
        }

        public AccessModifiers accessModifiers() {
            return accessModifiers;
        }

        public String name() {
            return name;
        }

        public DataType type() {
            return type;
        }

        public List<Parameter> parameters() {
            return parameters;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Method) obj;
            return Objects.equals(this.path, that.path) &&
                    Objects.equals(this.accessModifiers, that.accessModifiers) &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.type, that.type) &&
                    Objects.equals(this.parameters, that.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, accessModifiers, name, type, parameters);
        }

        @Override
        public String toString() {
            return "Method[" +
                    "path=" + path + ", " +
                    "accessModifiers=" + accessModifiers + ", " +
                    "name=" + name + ", " +
                    "type=" + type + ", " +
                    "parameters=" + parameters + ']';
        }

        }

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

            // 处于方法中，如果行为空，或无缩进，则视作方法结束
            if( ( line.i == 0 || line.line.isEmpty() ) && isInMethod) {
                isInMethod = false;
                METHODS.add(DemoMethod);
                DemoMethod = null;
                defineVarContext = new HashMap<>();
            }

            // 若没有缩进，则视为方法
            if(line.i == 0) {
                MethodParser methodParser = new MethodParser(line.line);
                    DemoMethod = methodParser.method.toMethod(path);
                    isInMethod = true;
                    // 创建定义上下文
                    for(Parameter p : methodParser.getParameterParser().getParameters()) {
                        defineVarContext.put(p.name, p.dataType);
                    }
            } else {
                // 方法体内容判断
                if(!isInMethod) throw new UnexpectedStatementException("atPath: " + script.getPath().toString() + "  ;atLine" + line.i);

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
                }

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
                method -> method.path.isPack(this.path) && method.modifiers.contains(MethodModifiers.LOAD)
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

    record Line(int i, String line) {}
}
