package com.ffanxxy.minepyloader.minepy.loader.Statement.type;

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
    BYTE("byte"),
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

    WORLD("World"),

    CLASS("Class"),

    // 不可使用
    VAR("Var"),
    LITERAL_STRING("LiteralString"),
    LITERAL_INTEGER("LiteralInteger"),
    LITERAL_FLOAT("LiteralFloat"),
    LITERAL_DOUBLE("LiteralDouble"),
    LITERAL_BOOLEAN("LiteralBoolean"),
    LITERAL_NULL("LiteralNull");

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

    public boolean isLiteral() {
        return switch (this) {
            case LITERAL_STRING , LITERAL_INTEGER, LITERAL_FLOAT , LITERAL_DOUBLE , LITERAL_BOOLEAN , LITERAL_NULL -> true;
            default -> false;
        };
    }

    /**
     * 判断是否同类型，对于表面量，会判断为原始类型
     * @param dataType 另一个数据类型
     * @return true为相同
     */
    public boolean isSameTypeAs(DataType dataType) {
        if(this == OBJECT) return true;

        switch (this) {
            case STRING , LITERAL_STRING -> {
                return dataType == STRING || dataType == LITERAL_STRING;
            }
            case INT , LITERAL_INTEGER -> {
                return dataType == INT || dataType == LITERAL_INTEGER;
            }
            case FLOAT , LITERAL_FLOAT -> {
                return dataType == FLOAT || dataType == LITERAL_FLOAT;
            }
            case DOUBLE , LITERAL_DOUBLE -> {
                return dataType == DOUBLE || dataType == LITERAL_DOUBLE;
            }
            case BOOLEAN , LITERAL_BOOLEAN-> {
                return dataType == BOOLEAN || dataType == LITERAL_BOOLEAN;
            }
            case NULL , LITERAL_NULL -> {
                return dataType == NULL || dataType == LITERAL_NULL;
            }
            default -> {
                return this == dataType;
            }
        }
    }
}
