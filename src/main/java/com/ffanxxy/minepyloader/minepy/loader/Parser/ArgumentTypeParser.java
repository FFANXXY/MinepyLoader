package com.ffanxxy.minepyloader.minepy.loader.Parser;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.LiteralValueParser;

import java.util.*;

public class ArgumentTypeParser {
    private final List<Parameter> parameters;

    public ArgumentTypeParser(String line) {
        String args = line.substring(
                line.indexOf("(") + 1,
                line.lastIndexOf(")")
        );

        if (args.isEmpty()) {
            parameters = new ArrayList<>();
            return;
        }

        ProcessResult result = processQuotedStrings(args);
        Map<Integer, String> map = result.extractedMap;
        String str = result.replacedString;

        List<String> argList = Arrays.stream(str.split(",")).map(String::trim).toList();

        int nowStr = 0;
        List<Parameter> parameterList = new ArrayList<>();

        for (String s : argList) {
            LiteralValueParser.Type type = LiteralValueParser.parser(s);

            // 解析参数
            if (type == LiteralValueParser.Type.STRING) {
                // 如果是字符串，设置类型为 字面字符串，当检测到为字面字符串，将它的名字设置为字符串内容
                parameterList.add(new Parameter(DataType.LITERAL_STRING, map.get(nowStr)));
                nowStr++;
            } else if (type == LiteralValueParser.Type.BOOLEAN) {
                parameterList.add(new Parameter(DataType.LITERAL_BOOLEAN, s));
            } else if (type == LiteralValueParser.Type.INT) {
                parameterList.add(new Parameter(DataType.LITERAL_INTEGER, s));
            } else if (type == LiteralValueParser.Type.DOUBLE) {
                parameterList.add(new Parameter(DataType.LITERAL_DOUBLE, s));
            } else if (type == LiteralValueParser.Type.NULL) {
                parameterList.add(new Parameter(DataType.LITERAL_NULL, s));
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

    /**
     *  实例将字符串 {@code  "xxx",123, what's this, sad, "SADNESS", "1414515"}
     *  <p>提取为 {@code ("",123, what's this, sad, "", "1414515") }
     *
     * @param replacedString 将所有"xxxxx" 替换为""  后的结果
     * @param extractedMap 将所有字符串按顺序提取的Map
     */
    public record ProcessResult(String replacedString, Map<Integer, String> extractedMap) {}

    /**
     * 解析字面字符串
     * @param input 实参
     * @return 结果
     */
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
}
