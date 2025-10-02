package com.ffanxxy.minepyloader.minepy.loader.Parser;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.DataTypeHelper;
import com.ffanxxy.minepyloader.minepy.utils.loader.LiteralValueParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获得括号内参数的解析，所有的参数会被解析成{@code List<VarGetterNode>}，如果它是字面量，则会将字面值作为名称，如果不是，则会返回一个变量
 *
 * @author FFANXXY
 * @version ALPHA-0.4+
 */
public class ArgumentParser {
    private final List<VarGetterNode> arguments;

    public ArgumentParser(ScriptParserLineContext ctx) {
        String line = ctx.line();
        String args;
        if(line.endsWith(")")) {
            args = line.substring(
                    line.indexOf("(") + 1,
                    line.lastIndexOf(")")
            );
        } else {
            args = line;
        }

        if (args.isEmpty()) {
            arguments = new ArrayList<>();
            return;
        }

        // 递归处理所有层级的字符串字面量
        ProcessResult result = processNestedStrings(args);
        Map<Integer, String> map = result.extractedMap;
        String str = result.replacedString;

        List<String> argList = splitArguments(str);

        int nowStr = 0;
        List<VarGetterNode> nodes = new ArrayList<>();

        for (String arg : argList) {
            LiteralValueParser.Type type = LiteralValueParser.parser(arg);

            switch (type) {
                case STRING -> {
                    nodes.add(StatementManager.getVarGetterNode("\"" + map.get(nowStr) + "\"", ctx));
                    nowStr++;
                }
                case CHAR -> {
                    nodes.add(StatementManager.getVarGetterNode("'" + map.get(nowStr) + "'", ctx));
                    nowStr++;
                }
                case METHOD_CALL -> {
                    // 对于方法调用，我们需要恢复其中可能被提取的字符串
                    String restoredMethod = restoreExtractedStrings(arg, map);
                    nodes.add(StatementManager.getVarGetterNode(restoredMethod, ctx));
                }
                default -> nodes.add(StatementManager.getVarGetterNode(arg, ctx));
            }
        }

        arguments = new ArrayList<>(nodes);
    }

    public List<VarGetterNode> getArguments() {
        return arguments;
    }

    /**
     * 递归处理嵌套结构中的字符串字面量
     */
    public static ProcessResult processNestedStrings(String input) {
        StringBuilder resultBuilder = new StringBuilder();
        Map<Integer, String> extractedMap = new LinkedHashMap<>();
        int quoteCount = 0;

        processNestedStringsRecursive(input, 0, input.length(), resultBuilder, extractedMap, quoteCount);

        return new ProcessResult(resultBuilder.toString(), extractedMap);
    }

    private static int processNestedStringsRecursive(String input, int start, int end,
                                                     StringBuilder resultBuilder,
                                                     Map<Integer, String> extractedMap,
                                                     int quoteCount) {
        int i = start;
        while (i < end) {
            char c = input.charAt(i);

            if (c == '"' || c == '\'') {
                // 处理字符串字面量
                char quoteChar = c;
                StringBuilder currentContent = new StringBuilder();
                boolean escaping = false;
                i++; // 跳过开始引号

                while (i < end) {
                    char currentChar = input.charAt(i);

                    if (escaping) {
                        currentContent.append(currentChar);
                        escaping = false;
                    } else if (currentChar == '\\') {
                        escaping = true;
                    } else if (currentChar == quoteChar) {
                        // 结束引号
                        extractedMap.put(quoteCount, currentContent.toString());
                        resultBuilder.append(quoteChar).append(quoteChar); // 替换为空字符串
                        quoteCount++;
                        i++; // 跳过结束引号
                        break;
                    } else {
                        currentContent.append(currentChar);
                    }
                    i++;
                }
            } else if (c == '(') {
                // 处理嵌套方法调用
                resultBuilder.append(c);
                i++;
                int depth = 1;
                int methodStart = i;

                while (i < end && depth > 0) {
                    char currentChar = input.charAt(i);
                    if (currentChar == '(') {
                        depth++;
                    } else if (currentChar == ')') {
                        depth--;
                    }

                    if (depth > 0) {
                        resultBuilder.append(currentChar);
                    }
                    i++;
                }

                // 递归处理嵌套方法内的参数
                if (methodStart < i - 1) {
                    StringBuilder nestedBuilder = new StringBuilder();
                    int newQuoteCount = processNestedStringsRecursive(input, methodStart, i - 1,
                            nestedBuilder, extractedMap, quoteCount);
                    // 替换结果中嵌套方法的内容
                    resultBuilder.setLength(resultBuilder.length() - (i - methodStart - 1));
                    resultBuilder.append(nestedBuilder);
                    quoteCount = newQuoteCount;
                }

                if (i <= end) {
                    resultBuilder.append(')');
                }
            } else {
                resultBuilder.append(c);
                i++;
            }
        }

        return quoteCount;
    }

    /**
     * 恢复被提取的字符串到方法调用中
     */
    private static String restoreExtractedStrings(String methodCall, Map<Integer, String> extractedMap) {
        StringBuilder restored = new StringBuilder();
        Pattern emptyQuotes = Pattern.compile("(['\"])\\1"); // 匹配 "" 或 ''

        Matcher matcher = emptyQuotes.matcher(methodCall);
        int lastIndex = 0;
        int quoteIndex = 0;

        while (matcher.find()) {
            restored.append(methodCall, lastIndex, matcher.start());
            if (quoteIndex < extractedMap.size()) {
                String originalContent = extractedMap.get(quoteIndex);
                restored.append(matcher.group().charAt(0))
                        .append(originalContent)
                        .append(matcher.group().charAt(0));
                quoteIndex++;
            } else {
                restored.append(matcher.group());
            }
            lastIndex = matcher.end();
        }

        restored.append(methodCall.substring(lastIndex));
        return restored.toString();
    }

    /**
     * 分割参数，考虑嵌套结构
     */
    private List<String> splitArguments(String input) {
        List<String> argList = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        int depth = 0; // 括号嵌套深度
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 处理引号内的内容（忽略括号计数）
            if (inQuotes) {
                currentArg.append(c);
                if (c == '\\') {
                    // 转义字符，跳过下一个字符
                    if (i + 1 < input.length()) {
                        currentArg.append(input.charAt(++i));
                    }
                } else if (c == quoteChar) {
                    inQuotes = false;
                }
                continue;
            }

            // 处理引号开始
            if (c == '"' || c == '\'') {
                inQuotes = true;
                quoteChar = c;
                currentArg.append(c);
                continue;
            }

            // 处理括号
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }

            // 处理参数分隔符
            if (c == ',' && depth == 0) {
                // 只在最外层分割逗号
                argList.add(currentArg.toString().trim());
                currentArg.setLength(0);
            } else {
                currentArg.append(c);
            }
        }

        // 添加最后一个参数
        if (!currentArg.isEmpty()) {
            argList.add(currentArg.toString().trim());
        }

        return argList;
    }

    /**
     * 处理结果记录
     */
    public record ProcessResult(String replacedString, Map<Integer, String> extractedMap) {}
}
