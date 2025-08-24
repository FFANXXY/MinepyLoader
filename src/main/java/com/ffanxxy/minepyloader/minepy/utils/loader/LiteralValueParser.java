package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import javax.xml.crypto.Data;

public class LiteralValueParser {
    /**
     * 获得字面量类型，应该将参数处理为["","",a,b,1,null]的格式导入
     * @param value 字面值
     * @return {@link DataType} 见字面量类型定义，如果为{@link DataType#VAR}则为一个方法调用
     */
    public static Type parser(String value) {
        if(value.equals("\"\"")) {
            return Type.STRING;
        } else if(value.equals("''")) {
            return Type.CHAR;
        } else if(value.equals("null")) {
            return Type.NULL;
        } else if(value.equals("true") || value.equals("false")) {
            return Type.BOOLEAN;
        } else if(getNumberType(value) != null) {
            return getNumberType(value);
        } else if(StatementManager.detectCodeType(value) == StatementManager.CodeType.METHOD_CALL) {
            return Type.METHOD_CALL;
        } else {
            return Type.ELSE;
        }
    }

    public enum Type {
        STRING,
        CHAR,
        NULL,
        DOUBLE,
        INT,
        BOOLEAN,
        METHOD_CALL,
        ELSE;
    }

    public static Type getNumberType(String str) {
        try {
            Integer.parseInt(str);
            return Type.INT;
        } catch (Exception e) {
            try {
                Double.parseDouble(str);
                return Type.DOUBLE;
            } catch (Exception e1) {
                return null;
            }
        }
    }
}
