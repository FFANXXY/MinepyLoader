package com.ffanxxy.minepyloader.minepy.loader.Statement;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.InternalMethods;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.ReturnNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.LiteralValueParser;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获得语句，将语句作为一个{@link RunnableNode}传出，并设置形参的索引。
 */
public class StatementManager {

    public RunnableNode node;

    private CodeType codeType;

    public StatementManager(ScriptParserLineContext context) {
        String line = context.line();
        Map<String, DataType> defineContext = context.defineVarContext();
        List<String> imports = context.imports();

        if(line.startsWith("return")) {
            // 对于return xxx ， 获得第二项
            String var = line.split("\\s+")[1].trim();

            this.node = new ReturnNode(getVarGetterNode(var,defineContext));

            return;
        }

        // 进行类型检测
        CodeType type = detectCodeType(line);
        if(type == null) throw new RuntimeException("CodeType is NULL");
        this.codeType = type;

        this.node = switch (type) {
            // 方法调用
            case METHOD_CALL -> parserMethod(context);
            // 变量声明
            case VARIABLE_DECLARATION -> parserVD(line, defineContext);
            default -> null;
        };
    }

    public CodeType getCodeType() {
        return codeType;
    }

    /**
     * 解析变量定义，获得变量定义节点
     * @param line 行
     * @return 定义节点
     */
    public static VariableDeclarationNode parserVD(String line, Map<String, DataType> defineContext) {
        int spi = line.indexOf("=");
        String value = null;
        if(spi != -1) {
            value = line.substring(spi + 1).trim();
            List<String> defines =  Arrays.stream(line.substring(0,spi).trim().split("\\s+")).map(String::trim).toList();
            DataType dataType = DataType.fromName(defines.get(0));
            String name = defines.get(1);
            // 初始化
            VarGetterNode v1 = getVarGetterNode(value, defineContext);

            return new VariableDeclarationNode(dataType, name, v1);
        } else {
            // 无初始化定义
            List<String> defines =  Arrays.stream(line.trim().split("\\s+")).map(String::trim).toList();
            DataType dataType = DataType.fromName(defines.get(0));
            String name = defines.get(1);

            return new VariableDeclarationNode(dataType, name);
        }
    }

    /**
     * 获得变量赋值节点
     * @param value 值
     * @return 节点
     */
    public static VarGetterNode getVarGetterNode(String value, Map<String, DataType> defineContext) {
        // 以 “" 包围的字面量
        if (value.startsWith("\"") && value.endsWith("\"")) {
            // Type is String, get context as String to create VIN
            return new VarGetterNode(VarGetterNode.InitType.LIT_STRING, value.substring(1, value.length() - 1), defineContext);
        } else if (value.equals("true")) {
            // if value equals true
            return new VarGetterNode(VarGetterNode.InitType.LIT_BOOLEAN, "true", defineContext);
        } else if (value.equals("false")) {
            // equals false
            return new VarGetterNode(VarGetterNode.InitType.LIT_BOOLEAN, "false", defineContext);
        } else if (value.equals("null")) {
            /// isNull
            return new VarGetterNode(VarGetterNode.InitType.LIT_NULL, "null", defineContext);
        } else if (LiteralValueParser.getNumberType(value) == LiteralValueParser.Type.INT) {
            // get NumberType if it is int
            return new VarGetterNode(VarGetterNode.InitType.LIT_INT, value, defineContext);
        } else if (LiteralValueParser.getNumberType(value) == LiteralValueParser.Type.DOUBLE) {
            // if it is double
            return new VarGetterNode(VarGetterNode.InitType.LIT_DOUBLE, value, defineContext);
        } else if (detectCodeType(value) == CodeType.METHOD_CALL) {
            // 方法调用，例如Entity.new()
            return new VarGetterNode(VarGetterNode.InitType.METHOD, value, defineContext);
        } else if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            // 选自变量
            return new VarGetterNode(VarGetterNode.InitType.VAR, value, defineContext);
        } else {
            // else it may be Operation
            return new VarGetterNode(VarGetterNode.InitType.OPERATION, value, defineContext);
        }
    }


    public static RunnableNode parserMethod(ScriptParserLineContext context) {
        String line = context.line();
        var defineContext = context.defineVarContext();
        // 检查导入
        var ims = context.imports();
        // test.abc

        String who = line.substring(0, line.indexOf("("));
//        String methodName = who.substring(who.lastIndexOf("."));

        if(!who.contains(".") || who.startsWith("mpy.")) {
            return parserInternalMethods(line);
        }

        return new MethodCallNode(who, new ParameterParser(line).parameters, defineContext);
    }


    public static RunnableNode parserInternalMethods(String line) {
        String who = line.substring(0, line.indexOf("("));

        if(!who.contains(".")) {
            who = "mpy." + who;
        }

        return InternalMethods.get(who, new ParameterParser(line).parameters);
    }

    /**
     * 变量获值
     */
    public static class VarGetterNode implements RunnableNode {

        private InitType type;
        private String var;

        private MethodCallNode methodCallNode;

        // 初始化类型，值，名称
        public VarGetterNode(InitType type, String var, Map<String, DataType> defineContext) {
            this.type =type;
            this.var = var;

            if(type == InitType.METHOD) {
                this.methodCallNode = new MethodCallNode(var.substring(0, var.indexOf("(")), new ParameterParser(var).getParameters(), defineContext);
            }
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
             return switch (type) {
                case LIT_STRING -> Variable.ofString("%LIT", var);
                case LIT_BOOLEAN -> Variable.ofBoolean("%LIT", Boolean.parseBoolean(var));
                 case LIT_INT ->  Variable.ofInteger("%LIT", Integer.parseInt(var));
                 case LIT_DOUBLE -> Variable.ofDouble("%LIT", Double.parseDouble(var));
                 case LIT_NULL -> Variable.NULL();
                 case METHOD -> methodCallNode.runWithArg(variableMap);
                 case OPERATION -> {
                     // ... skip
                     yield Variable.VOID();
                 }
                 case VAR -> Minepy.getFromSAN(var, variableMap);
            };
        }

        public enum InitType {
            METHOD,
            VAR, // 已有的变量
            OPERATION, // 计算
            LIT_STRING,
            LIT_INT,
            LIT_DOUBLE,
            LIT_BOOLEAN,
            LIT_NULL;
        }
    }

    /**
     * 方法语句调用存储
     */
    public static class MethodCallNode implements RunnableNode {
        private final String method;
        private final List<Parameter> InParameters;

        public MethodCallNode(String method, List<Parameter> InParameters, Map<String, DataType> defineContext) {
            this.method = method;
            this.InParameters = InParameters;
            // 处理参数
            for (Parameter p : InParameters) {
                if (p.dataType == DataType.VAR) {
                    // 从定义上下文获得变量类型
                    p.dataType = defineContext.get(p.name);
                }
            }
        }


        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            // 获得方法
            Minepy.Method mtd = MethodHelper.getMethod(this.method, this.InParameters);
            
            // 获得形参
            List<Parameter> parameters = mtd.parameters();

            if(parameters.size() != InParameters.size()) throw new RuntimeException("The number of parameters is wrong: " + method);
            if(parameters.isEmpty())  return mtd.run(new ArrayList<>());

            List<Argument> resultRunArgs = new ArrayList<>();

            // 对输入的参数进行解析
            for (int i = 0; i < InParameters.size(); i++) {
                var methodParameter =  parameters.get(i);
                var inParameter = InParameters.get(i);

                if(! methodParameter.dataType.isSameTypeAs(inParameter.dataType)) throw new RuntimeException("The required parameter types are not paired: when-" + method);

                if(inParameter.dataType.isLiteral()) {
                    Variable<?> variable;

                    switch (inParameter.dataType) {
                        case LITERAL_STRING -> variable = Variable.ofString(inParameter.name, inParameter.name);
                        case LITERAL_INTEGER -> variable = Variable.ofInteger(inParameter.name, Integer.parseInt(inParameter.name));
                        default -> variable = Variable.NULL();
                    }

                    resultRunArgs.add(
                            new Argument(methodParameter.name, variable)
                    );
                } else {
                    Variable<?> variable = Minepy.getFromSAN(inParameter.name, variableMap);
                    if(variable == null) throw  new RuntimeException("Unknown Variable: " + inParameter.name);
                    resultRunArgs.add(
                            new Argument(methodParameter.name, variable)
                    );
                }
            }

            return mtd.run(resultRunArgs);
        }
    }

    public static class VariableDeclarationNode implements RunnableNode {

        private final DataType dataType;
        private final String name;
        private final VarGetterNode node;

        public DataType getDataType() {
            return this.dataType;
        }
        public String getName() {
            return this.name;
        }

        public VariableDeclarationNode(DataType dataType, String name) {
            this.dataType = dataType;
            this.name = name;
            this.node = null;
        }

        public VariableDeclarationNode(DataType dataType, String name, VarGetterNode value) {
            this.dataType = dataType;
            this.name = name;
            this.node = value;
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            Variable<?> variable = Variable.createWithNewName(this.name, node.runWithArg(variableMap));

            // Double转Float
            // 对象转换处理
            if((variable.isDataType(DataType.DOUBLE) || variable.isDataType(DataType.LITERAL_DOUBLE)) &&
                dataType == DataType.FLOAT) {
                variable = variable.toFloat();
            }

            variableMap.put(new Minepy.ScopeAndName(1,variable.getName()),variable);
            return Variable.VOID();
        }
    }

    public static class ParameterParser {
        private final List<Parameter> parameters;

        public ParameterParser(String line) {
            String args = line.substring(
                    line.indexOf("(") + 1,
                    line.lastIndexOf(")")
            );

            if(args.isEmpty()) {
                parameters = new ArrayList<>();
                return;
            }

            ProcessResult result = processQuotedStrings(args);
            Map<Integer, String> map = result.extractedMap;
            String str = result.replacedString;

            List<String> argList = Arrays.stream(str.split(",")).map(String::trim).toList();

            int nowStr = 0;
            List<Parameter> parameterList = new ArrayList<>();

            for(String s : argList) {
                LiteralValueParser.Type type = LiteralValueParser.parser(s);

                // 解析参数
                if(type == LiteralValueParser.Type.STRING) {
                    // 如果是字符串，设置类型为 字面字符串，当检测到为字面字符串，将它的名字设置为字符串内容
                    parameterList.add(new Parameter(DataType.LITERAL_STRING, map.get(nowStr)));
                    nowStr++;
                } else if(type == LiteralValueParser.Type.BOOLEAN) {
                    parameterList.add(new Parameter(DataType.LITERAL_BOOLEAN,s));
                }else if(type == LiteralValueParser.Type.INT) {
                    parameterList.add(new Parameter(DataType.LITERAL_INTEGER,s));
                }else if(type == LiteralValueParser.Type.DOUBLE) {
                    parameterList.add(new Parameter(DataType.LITERAL_DOUBLE, s));
                }else if(type == LiteralValueParser.Type.NULL) {
                    parameterList.add(new Parameter(DataType.LITERAL_NULL,s));
                } else {
                    // 暂时无法确定变量类型，通过运行时的定义上下文重获得DataType
                    parameterList.add(new Parameter(DataType.VAR, s));
                }
            }

            parameters = new ArrayList<>(parameterList);
        }

        public List<Parameter> getParameters() {
            return parameters;
        }
    }

    public record ProcessResult(String replacedString, Map<Integer, String> extractedMap) {
    }

    public static ProcessResult processQuotedStrings(String input) {
        StringBuilder resultBuilder = new StringBuilder();
        Map<Integer, String> extractedMap = new LinkedHashMap<>();
        boolean inQuotes = false;
        boolean escaping = false;
        StringBuilder currentContent = new StringBuilder();
        int quoteCount = 0;

        for (char c : input.toCharArray()) {
            if (!inQuotes) {
                // 引号外区域
                resultBuilder.append(c);
                if (c == '"') {
                    // 进入引号区域
                    inQuotes = true;
                    escaping = false;
                }
            } else {
                // 引号内区域
                if (escaping) {
                    // 转义状态：保留转义序列
                    currentContent.append(c);
                    escaping = false;
                } else {
                    if (c == '\\') {
                        // 开始转义序列
                        currentContent.append(c);
                        escaping = true;
                    } else if (c == '"') {
                        // 结束引号区域
                        resultBuilder.append('"'); // 保留结束引号
                        inQuotes = false;
                        // 保存提取的内容
                        extractedMap.put(quoteCount++, currentContent.toString());
                        currentContent.setLength(0); // 重置内容
                    } else {
                        // 普通字符：记录但不添加到结果
                        currentContent.append(c);
                    }
                }
            }
        }

        // 处理未闭合的引号
        if (inQuotes) {
            // 将未闭合引号内容添加到结果
            resultBuilder.append(currentContent);
        }

        return new ProcessResult(resultBuilder.toString(), extractedMap);
    }


    public RunnableNode get() {
        return this.node;
    }

    public static CodeType detectCodeType(String code) {
        String trimmed = code.trim();
        if (trimmed.isEmpty()) return null;

        // 1. 检测变量声明：以数据类型关键字开头
        if (isVariableDeclaration(trimmed)) {
            return CodeType.VARIABLE_DECLARATION;
        }

        // 2. 检测控制语句：if/while/for 开头的逻辑判断
        if (isControlStatement(trimmed)) {
            return CodeType.LOGICAL_EXPRESSION;
        }

        // 3. 检测方法调用：方法名后跟括号
        if (isMethodCall(trimmed)) {
            return CodeType.METHOD_CALL;
        }

        // 4. 检测逻辑表达式：包含逻辑/比较运算符
        if (hasLogicalOperators(trimmed)) {
            return CodeType.LOGICAL_EXPRESSION;
        }

        // 5. 默认归类为变量运算
        return CodeType.VARIABLE_OPERATION;
    }

    public enum CodeType {
        VARIABLE_DECLARATION,
        LOGICAL_EXPRESSION,
        METHOD_CALL,
        VARIABLE_OPERATION
    }

    // 辅助方法：检测变量声明
    private static boolean isVariableDeclaration(String s) {
        // 匹配基本类型 + 变量名 [+ 初始化] + 分号
        String regex = "^(String|int|double|float|byte|boolean|Block|BlockEntity|BlockState|Entity|Player|Text|Item|ItemStack)\\s+[a-zA-Z_]\\w*\\s*(=\\s*[^;]+)?\\s*$";
        return s.matches(regex);
    }

    // 辅助方法：检测控制语句（if/while/for）
    private static boolean isControlStatement(String s) {
        String lower = s.toLowerCase();
        return lower.startsWith("if") ||
                lower.startsWith("while") ||
                lower.startsWith("for") ||
                lower.startsWith("switch");
    }

    // 辅助方法：检测方法调用
    private static boolean isMethodCall(String s) {
        // 匹配 [对象.]方法名(参数) [+ 分号]
        String regex = "^\\s*\\w+(\\.\\w+)*\\s*\\(.*\\)\\s*;?\\s*$";
        return s.matches(regex);
    }

    // 辅助方法：检测逻辑运算符
    private static boolean hasLogicalOperators(String s) {
        Pattern pattern = Pattern.compile("&&|\\|\\||==|!=|>=|<=|>|<|!");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
