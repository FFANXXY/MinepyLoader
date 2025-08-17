package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedStatementException;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnknownValueException;

public enum DataType {
    OBJECT("Object"),
    STRING("String"),
    CHAR("char"),
    INT("int"),
    FLOAT("float"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    LIST("List"),
    ARRAY("Array"),
    MAP("Map"),
    // 其它数据类型
    VOID("void"),
    NULL("null"),
    UNDEFINE(""),
    //游戏数据类型
    ENTITY("Entity"),
    PLAYER("Player"),

    BLOCK("Block"),
    BLOCKSTATE("BlockState"),
    BLOCKENTITY("BlockEntity"),
    BLOCKPOS("BlockPos"),

    POS("Pos"),
    ROTATION("Rotation"),
    VEC3("Vec3"),

    ITEM("Item"),
    ITEMSTACK("ItemStack"),

    TEXT("Text"),

    CLASS("Class"),

    // 不可使用
    PARA("Para"),
    LITERAL_STRING("LiteralString"),
    LITERAL_INTEGER("LiteralInteger"),
    LITERAL_FLOAT("LiteralFloat"),
    LITERAL_DOUBLE("LiteralDouble"),
    LITERAL_BOOLEAN("LiteralBoolean");

    private final String name;

    DataType(String name) {
        this.name = name;
    }

    public static DataType fromName(String name) {
        for (DataType modifier : values()) {
            if (modifier.name.equals(name)) {
                return modifier;
            }
        }
        throw new IllegalArgumentException("Invalid data type:" + name);
    }


    public String getName() {
        return name;
    }
}
