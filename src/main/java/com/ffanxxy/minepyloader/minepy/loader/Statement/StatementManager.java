package com.ffanxxy.minepyloader.minepy.loader.Statement;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.InternalMethods;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementManager {

    public RunnableNode node;

    private CodeType codeType;

    public StatementManager(String line, Map<String, DataType> defineContext) {

        // 进行类型检测
        CodeType type = detectCodeType(line);
        if(type == null) throw new RuntimeException("CodeType is NULL");
        this.codeType = type;

        this.node = switch (type) {
            case METHOD_CALL -> callMethod(line, defineContext);
            case VARIABLE_DECLARATION -> parserVD(line);
            default -> null;
        };
    }

    public CodeType getCodeType() {
        return codeType;
    }

    /**
     * 解析变量定义，获得变量定义节点
     * @param line
     * @return
     */
    public static VariableDeclarationNode parserVD(String line) {
        int spi = line.indexOf("=");
        String value = null;
        if(spi != -1) {
            value = line.substring(spi + 1).trim();
            List<String> defines =  Arrays.stream(line.substring(0,spi).trim().split("\\s+")).map(String::trim).toList();
            DataType dataType = DataType.fromName(defines.get(0));
            String name = defines.get(1);

            Object v1;
            if(value.startsWith("\"") && value.endsWith("\"")) {
                v1 = value.substring(1, value.length() -1);
            } else {
                v1 = new Object();
            }

            return new VariableDeclarationNode(dataType, name, v1);
        } else {
            List<String> defines =  Arrays.stream(line.trim().split("\\s+")).map(String::trim).toList();
            DataType dataType = DataType.fromName(defines.get(0));
            String name = defines.get(1);

            return new VariableDeclarationNode(dataType, name);
        }

    }

    public static RunnableNode callMethod(String line, Map<String, DataType> defineContext) {
        String who = line.substring(0, line.indexOf("("));

        if(!who.contains(".") || who.startsWith("mpy.")) {
            return callInternalMethods(line);
        }

        return new MethodCallNode(who, new ParameterParser(line).parameters, defineContext);
    }


    public static RunnableNode callInternalMethods(String line) {
        String who = line.substring(0, line.indexOf("("));

        if(!who.contains(".")) {
            who = "mpy." + who;
        }

        return InternalMethods.get(who, new ParameterParser(line).parameters);
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
            // 获得变量
            List<Minepy.Method> methods = Minepy.METHODS.stream().filter(
                    m -> (m.path() + "." + m.name()).equals(method) //是否为调用的方法
            ).toList();

            if(methods.isEmpty()) throw new RuntimeException("Unknown method:" + this.method);

            Minepy.Method mtd = null;
            // 具体方法判断
            // 允许方法同名，模拟重写
            if(methods.size() == 1) {
                mtd = methods.get(0);
            } else {
                // 精确判断
                List<Minepy.Method> detailMethods = methods.stream()
                        .filter(
                                m1 -> m1.parameters().size() == this.InParameters.size()
                        ).filter(
                                m1 -> m1.parameters().stream().allMatch(
                                        // 参数不应该相同，因此直接判断
                                        p -> InParameters.get(methods.indexOf(m1)).dataType.isSameTypeAs(p.dataType)
                                )
                        ).toList();
                if(detailMethods.isEmpty()) throw new RuntimeException("There are no known methods that meet the parameters: " + this.method);
                if(detailMethods.size() > 1) throw new RuntimeException("Surprising err: too more methods has same parameters: " + this.method);

                mtd = detailMethods.get(0);
            }
            
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
        private final Object defaultValue;

        public DataType getDataType() {
            return this.dataType;
        }
        public String getName() {
            return this.name;
        }

        public VariableDeclarationNode(DataType dataType, String name) {
            this.dataType = dataType;
            this.name = name;
            this.defaultValue = null;
        }

        public VariableDeclarationNode(DataType dataType, String name, Object value) {
            this.dataType = dataType;
            this.name = name;
            this.defaultValue = value;
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            // 防止错误，理论代码正确应无问题
            String vs = "";

            if(defaultValue instanceof String v) {
                vs = v;
            }

            Variable<?> variable = switch (dataType) {
                case STRING -> Variable.ofString(name, vs);
                // 扩展...
                default -> Variable.ofString(name,  (String) defaultValue);
            };
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
                if(s.equals("\"\"")) {
                    parameterList.add(new Parameter(DataType.LITERAL_STRING, map.get(nowStr)));
                    nowStr++;
                } else {
                    // 检索上下文
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
