package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Parser.ArgumentTypeParser;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ffanxxy.minepyloader.minepy.loader.Parser.ArgumentTypeParser.processQuotedStrings;

public class ValueGetter {
    public static VarGetterNode getWhenReading(String var, ScriptParserLineContext context) {
        return StatementManager.getVarGetterNode(var, context);
    }

    public static List<Variable<?>> getArguments(String args) {
        ArgumentTypeParser.ProcessResult result = processQuotedStrings(args);
        Map<Integer, String> map = result.extractedMap();
        String str = result.replacedString();

        List<String> argList = Arrays.stream(str.split(",")).map(String::trim).toList();

        int nowStr = 0;
        List<Variable<?>> vars = new ArrayList<>();

        for (String s : argList) {
            LiteralValueParser.Type type = LiteralValueParser.parser(s);

            switch (type) {
                case STRING -> {
                    // 如果是字符串，设置类型为 字面字符串，当检测到为字面字符串，将它的名字设置为字符串内容
                    vars.add(Variable.ofString("%TEMP", map.get(nowStr)));
                    nowStr++;
                }
                case CHAR -> {
                    vars.add(Variable.ofChar("%TEMP", map.get(nowStr).charAt(0)));
                    nowStr++;
                }
                case BOOLEAN -> vars.add(Variable.ofBoolean("%TEMP", Boolean.parseBoolean(s)));
                case INT -> vars.add(Variable.ofInteger("%TEMP", Integer.parseInt(s)));
                case DOUBLE -> vars.add(Variable.ofDouble("%TEMP", Double.parseDouble(s)));
                case NULL -> vars.add(Variable.NULL());

                // 已废弃，直接获得变量类型
                // 暂时无法确定变量类型，通过运行时的定义上下文重获得DataType
//                case ELSE -> parameterList.add(new Parameter(DataType.VAR, s));
                case ELSE -> throw new RuntimeException();
            }
        }
        return vars;
    }
}
