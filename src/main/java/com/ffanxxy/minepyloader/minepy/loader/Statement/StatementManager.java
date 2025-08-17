package com.ffanxxy.minepyloader.minepy.loader.Statement;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.InternalMethods;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementManager {

    public RunnableNode node;

    public StatementManager(String line) {

        // 进行类型检测
        CodeType type = detectCodeType(line);
        if(type == null) throw new RuntimeException("CodeType is NULL");
        this.node = switch (type) {
            case METHOD_CALL -> callMethod(line);
            default -> null;
        };
    }

    public static RunnableNode callMethod(String line) {
        String who = line.substring(0, line.indexOf("("));

        if(!who.contains(".") || who.startsWith("mpy.")) {
            return callInternalMethods(line);
        }

        return null;
    }

    public static RunnableNode callInternalMethods(String line) {
        String who = line.substring(0, line.indexOf("("));

        if(!who.contains(".")) {
            who = "mpy." + who;
        }

        return InternalMethods.get(who, new ParameterParser(line).parameters);
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
                    parameterList.add(new Parameter(DataType.PARA, s));
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
        String regex = "^(int|double|float|char|boolean|String|byte|short|long)\\s+[a-zA-Z_]\\w*\\s*(=\\s*[^;]+)?\\s*;\\s*$";
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
