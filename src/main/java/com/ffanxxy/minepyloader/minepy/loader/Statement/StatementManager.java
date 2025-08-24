package com.ffanxxy.minepyloader.minepy.loader.Statement;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Parser.ArgumentTypeParser;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.*;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method.CallMethodNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.AssignmentNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VariableDeclarationNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.loader.LiteralValueParser;

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

            // 没有空格
            if(!line.contains(" ")) {
                this.node = new ReturnNode();
                return;
            }

            // 对于return xxx ， 获得第二项
            String var = line.substring(line.indexOf(" ")).trim();

            // 后为空
            if(var.isEmpty()) {
                this.node = new ReturnNode();
                return;
            }

            this.node = new ReturnNode(getVarGetterNode(var,context));
            return;

        } else if(line.startsWith("break")) {

            this.node = new BreakNode();
            return;

        }

        // 进行类型检测
        CodeType type = detectCodeType(line);
        if(type == null) throw new RuntimeException("CodeType is NULL");
        this.codeType = type;

        this.node = switch (type) {
            // 方法调用
            case METHOD_CALL -> parseMethod(context);
            // 变量声明
            case VARIABLE_DECLARATION -> parseVariableDeclaration(context);
            case VARIABLE_OPERATION -> parseVarOperation(context);
            case CONTROL_STATEMENT -> parseControlNode(context);
        };
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public static RunnableNode parseControlNode(ScriptParserLineContext context) {
        String line = context.line().trim();

        String substring = line.substring(
                line.indexOf("(") + 1,
                line.lastIndexOf(")")
        );

        if(line.startsWith("if")) {
            return new IfNode(substring, context);
        } else if(line.startsWith("while")) {
            return new WhileNode(substring, context);
        } else {
            throw new RuntimeException();
        }

    }


    public static AssignmentNode parseVarOperation(ScriptParserLineContext context) {
        String line = context.line();

        int spi = line.indexOf("=");

        if(spi == -1) throw new RuntimeException(line + " is not any statement");

        String value = line.substring(spi + 1).trim();
        String name = line.substring(0,spi).trim();

        return new AssignmentNode(name, getVarGetterNode(value, context));
    }

    /**
     * 解析变量定义，获得变量定义节点
     * @return 定义节点
     */
    public static VariableDeclarationNode parseVariableDeclaration(ScriptParserLineContext context) {
        String line = context.line();
        Map<String, DataType> defineContext = context.defineVarContext();

        int spi = line.indexOf("=");
        String value = null;
        if(spi != -1) {
            value = line.substring(spi + 1).trim();
            List<String> defines =  Arrays.stream(line.substring(0,spi).trim().split("\\s+")).map(String::trim).toList();
            DataType dataType = DataType.fromName(defines.get(0));
            String name = defines.get(1);
            // 初始化
            VarGetterNode v1 = getVarGetterNode(value, context);

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
    public static VarGetterNode getVarGetterNode(String value, ScriptParserLineContext context) {
        // 以 “" 包围的字面量
        if (value.startsWith("\"") && value.endsWith("\"")) {
            // Type is String, get context as String to create VIN
            return new VarGetterNode(VarGetterNode.InitType.LIT_STRING, value.substring(1, value.length() - 1), context);
        } else if (value.startsWith("'") && value.endsWith("'")) {
            return new VarGetterNode(VarGetterNode.InitType.LIT_CHAR, value.substring(1,2), context);
        } else if (value.equals("true")) {
            // if value equals true
            return new VarGetterNode(VarGetterNode.InitType.LIT_BOOLEAN, "true", context);
        } else if (value.equals("false")) {
            // equals false
            return new VarGetterNode(VarGetterNode.InitType.LIT_BOOLEAN, "false", context);
        } else if (value.equals("null")) {
            /// isNull
            return new VarGetterNode(VarGetterNode.InitType.LIT_NULL, "null", context);
        } else if (LiteralValueParser.getNumberType(value) == LiteralValueParser.Type.INT) {
            // get NumberType if it is int
            return new VarGetterNode(VarGetterNode.InitType.LIT_INT, value, context);
        } else if (LiteralValueParser.getNumberType(value) == LiteralValueParser.Type.DOUBLE) {
            // if it is double
            return new VarGetterNode(VarGetterNode.InitType.LIT_DOUBLE, value, context);
        } else if (detectCodeType(value) == CodeType.METHOD_CALL) {
            // 方法调用，例如Entity.new()
            return new VarGetterNode(VarGetterNode.InitType.METHOD, value, context);
        } else if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            // 选自变量
            return new VarGetterNode(VarGetterNode.InitType.VAR, value, context);
        } else {
            // else it may be Operation
            return new VarGetterNode(VarGetterNode.InitType.OPERATION, value, context);
        }
    }

    public static RunnableNode parseMethod(ScriptParserLineContext context) {
        String line = context.line();
        var defineContext = context.defineVarContext();
        // 检查导入
        var ims = context.imports();
        // test.abc

        String who = line.substring(0, line.indexOf("("));

        String methodName;
        if(who.contains(".")) {
            methodName = who.substring(0, who.lastIndexOf("."));
        } else {
            methodName = who;
        }


        if(InternalMethods.contains(methodName)) {
            return parserInternalMethods(line, context);
        }

        return new CallMethodNode(who, new ArgumentTypeParser(context).getParameters(), defineContext);
    }


    public static RunnableNode parserInternalMethods(String line, ScriptParserLineContext context) {
        String who = line.substring(0, line.indexOf("("));


        if(!PackageStructure.create(who).getFirst().equals("mpy")) {
            who = "mpy." + who;
        }

        return InternalMethods.get(who, new ArgumentTypeParser(context).getParameters(), context);
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
            return CodeType.CONTROL_STATEMENT;
        }

        // 3. 检测方法调用：方法名后跟括号
        if (isMethodCall(trimmed)) {
            return CodeType.METHOD_CALL;
        }

        // 4. 默认归类为变量运算
        return CodeType.VARIABLE_OPERATION;
    }

    public enum CodeType {
        VARIABLE_DECLARATION,
        CONTROL_STATEMENT,
        METHOD_CALL,
        VARIABLE_OPERATION
    }

    // 辅助方法：检测变量声明
    private static boolean isVariableDeclaration(String s) {
        // 匹配基本类型 + 变量名 [+ 初始化]
        String regex = "^(String|int|double|float|byte|boolean|Block|BlockEntity|BlockState|Entity|Player|Text|Item|ItemStack|World|char)\\s+[a-zA-Z_]\\w*\\s*(=\\s*[^;]+)?\\s*$";
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
}
