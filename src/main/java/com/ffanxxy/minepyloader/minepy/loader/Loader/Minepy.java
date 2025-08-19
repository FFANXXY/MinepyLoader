package com.ffanxxy.minepyloader.minepy.loader.Loader;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.AccessAndIndex;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ReturnNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.loader.scriptObject.Script;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedMethodException;
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

            // 不处在方法中，且行为空，则继续
            if(line.line.isEmpty() && !isInMethod) continue;

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
                    if(manager.get() instanceof StatementManager.VariableDeclarationNode node) {
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

    static interface Parser<T> {
        T get(int i);
    }

    /**
     * 方法解析器
     */
    public static class MethodParser implements Parser<MethodDefiner> {

        private final MethodDefiner method;
        private final ParameterParser parameterParser;

        public MethodParser(String string) {

            parameterParser = new ParameterParser(string.substring(
                    string.indexOf("(") + 1,
                    string.lastIndexOf(")")
            ));

            // 截取方法定义
            String methodDefine = string.substring(0, string.indexOf("(")).trim();

            List<String> words = new ArrayList<>(Arrays.stream(methodDefine.split("\\s+")).toList());
            
            Collections.reverse(words);


            if(words.size() < 2) throw new RuntimeException("There are too few middle keywords in the method definition: " + string);
            String name = words.get(0);
            DataType returnDatatype = DataType.fromName(words.get(1));
            AccessModifiers accessModifiers;
            // 是否有访问修饰符
            boolean hasAccessModifier = AccessModifiers.isModifier(words.get(words.size() -1));
            if(hasAccessModifier) {
                accessModifiers = AccessModifiers.fromName(words.get(words.size() -1));
            } else {
                accessModifiers = AccessModifiers.DEFAULT;
            }

            // 判断方法修饰符
            words.remove(0);
            words.remove(0);
            words.remove(words.size() -1);
            List<MethodModifiers> modifiers = new ArrayList<>();

            if(!words.isEmpty()) {
                for (String word : words) {
                    modifiers.add(MethodModifiers.fromName(word));
                }
            }

            method = new MethodDefiner(
                    accessModifiers,
                    modifiers,
                    name,
                    returnDatatype,
                    parameterParser.parameters
            );
        }

        /**
         * 获得方法
         *
         * @param i 无用参数
         * @return 解析出的方法
         */
        @Override
        public MethodDefiner get(int i) {
            return method;
        }

        public ParameterParser getParameterParser() {
            return parameterParser;
        }
    }

    public static class ParameterParser implements Parser<Parameter> {

        private final List<Parameter> parameters;
        
        public ParameterParser(String string) {
            String newStr = string.trim();
            if(newStr.isEmpty()) {
                parameters = new ArrayList<>();
                return;
            }
            parameters = Arrays.stream(newStr.split(",")).map(Parameter::new).toList();
        }

        @Override
        public Parameter get(int i) {
            return parameters.get(i);
        }

        public List<Parameter> getParameters() {
            return parameters;
        }
    }

    public static interface RunnableBlock {
        Variable<?> run(Map<ScopeAndName, Variable<?>> variableMap);
    }

    record Line(int i, String line) {}

    public static class Statement implements RunnableBlock {

        private RunnableNode node;

        public Statement(RunnableNode node) {
            this.node = node;
        }

        @Override
        public Variable<?> run(Map<ScopeAndName, Variable<?>> variableMap) {
            return node.runWithArg(variableMap);
        }
    }

    public static class Statements implements RunnableBlock {
        List<RunnableBlock> blocks = new ArrayList<>();

        Statements() {

        }

        public void join(RunnableBlock block) {
            blocks.add(block);
        }

        public void join(Statements statements) {
            blocks.addAll(statements.blocks);
        }

        /**
         * 对于语句组里的所有语句运行，运行初始化参数
         * @param arguments 实参
         * @return 返回值
         */
        public Variable<?> runWithArg(List<Argument> arguments) {
            Map<ScopeAndName, Variable<?>> variableMap = new HashMap<>();
            arguments.forEach(
                    argument -> variableMap.put(
                            new ScopeAndName(0, argument.getVariable().getName()),
                            argument.getVariable()
                    )
            );

            Variable<?> result;

            for (RunnableBlock block : blocks) {
                result = block.run(variableMap);
                if(block instanceof Statement statement) {
                    if(statement.node instanceof ReturnNode) return result;
                }
            }
            return Variable.VOID();
        }

        @Override
        public Variable<?> run(Map<ScopeAndName, Variable<?>> variableMap) {
            Variable<?> result;
            for (RunnableBlock block : blocks) {
                result = block.run(variableMap);
                if(block instanceof Statement statement) {
                    if(statement.node instanceof ReturnNode) return result;
                }
            }
            return Variable.VOID();
        }
    }
}
