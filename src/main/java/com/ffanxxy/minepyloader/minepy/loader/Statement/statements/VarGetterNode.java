package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Parser.ArgumentTypeParser;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Map;

/**
 * 变量获值
 */
public class VarGetterNode implements RunnableNode {

    private InitType type;
    private String var;

    private RunnableNode callMethodNode;

    // 初始化类型，值，名称
    public VarGetterNode(InitType type, String var, ScriptParserLineContext context) {
        this.type = type;
        this.var = var;

        if (type == InitType.METHOD) {
//            this.callMethodNode = new CallMethodNode(var.substring(0, var.indexOf("(")), new ArgumentTypeParser(var).getParameters(), context.defineVarContext());
            this.callMethodNode = StatementManager.parserMethod(ScriptParserLineContext.createWithNewLine(context, var));
        }
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        return switch (type) {
            case LIT_STRING -> Variable.ofString("%LIT", var);
            case LIT_BOOLEAN -> Variable.ofBoolean("%LIT", Boolean.parseBoolean(var));
            case LIT_INT -> Variable.ofInteger("%LIT", Integer.parseInt(var));
            case LIT_DOUBLE -> Variable.ofDouble("%LIT", Double.parseDouble(var));
            case LIT_NULL -> Variable.NULL();
            case METHOD -> callMethodNode.runWithArg(variableMap);
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
