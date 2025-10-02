package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.GameInterface.Text.MpyStyle;
import com.ffanxxy.minepyloader.GameInterface.Text.MpyText;
import com.ffanxxy.minepyloader.GameInterface.World.MpyDimension;
import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MpyList;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedDataTypeException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class Variable<T> {
    /**
     * 全局变量记录
     */
    public static final Map<AccessAndIndex, Variable<?>> GlobalVariables = new HashMap<>();

    private final String name;
    private final DataType dataType;
    private T value;

    private Variable(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    private Variable(String name, DataType dataType, T value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }

    /**
     * 只改变变量名，不改变值
     * @param name 名称
     * @param var 变量
     * @return 更名的变量
     */
    public static Variable<?> create(String name, Variable<?> var) {
        return new Variable<>(name, var.dataType, var.value);
    }


    public static Variable<?> create(String name, DataType dataType) {
        return new Variable<>(name, dataType);
    }

    // 泛型工厂

    public static <T> Variable<T> of(String name, DataType dataType) {
        return new Variable<>(name, dataType);
    }

    public static <T> Variable<T> of(String name, DataType dataType, T val) {
        return new Variable<>(name, dataType, val);
    }

    @SuppressWarnings("unchecked")
    public <U> Variable<U> getAs(DataType expectedType) {
        if (this.dataType == expectedType) {
            return (Variable<U>) this;
        }
        throw new UnexpectedDataTypeException();
    }

    // Void
    public static Variable<Object> VOID() {
        return new Variable<>("void", DataType.VOID);
    }
    public boolean isVoid() {
        return this.dataType == DataType.VOID;
    }

    //null
    public static Variable<Object> NULL() {
        return new Variable<>("null", DataType.NULL);
    }
    public Variable<Object> getAsNull() {
        if(this.dataType == DataType.NULL) return (Variable<Object>) this;
        throw new UnexpectedDataTypeException();
    }
    public boolean isNull() { return this.dataType == DataType.NULL; }

    //String
    public static Variable<String> ofString(String name) { return of(name, DataType.STRING);}
    public static Variable<String> ofString(String name, String val) { return of(name,DataType.STRING,val); }
    public Variable<String> getAsString() { return getAs(DataType.STRING); }

    //Char
    public static Variable<Character> ofChar(String name) { return of(name, DataType.CHAR); }
    public static Variable<Character> ofChar(String name, Character val) { return of(name, DataType.CHAR, val); }
    public Variable<Character> getAsChar() { return getAs(DataType.CHAR); }

    //Integer
    public static Variable<Integer> ofInteger(String name) { return of(name, DataType.INT); }
    public static Variable<Integer> ofInteger(String name, Integer val) { return of(name, DataType.INT, val); }
    public Variable<Integer> getAsInt() { return getAs(DataType.INT); }

    //Float
    public static Variable<Float> ofFloat(String name) { return of(name, DataType.FLOAT); }
    public static Variable<Float> ofFloat(String name, Float val) { return of(name, DataType.FLOAT, val); }
    public Variable<Float> getAsFloat() { return getAs(DataType.FLOAT); }

    //Double
    public static Variable<Double> ofDouble(String name) { return of(name, DataType.DOUBLE); }
    public static Variable<Double> ofDouble(String name, Double val) { return of(name, DataType.DOUBLE, val); }
    public Variable<Double> getAsDouble() { return getAs(DataType.DOUBLE); }

    //Boolean
    public static Variable<Boolean> ofBoolean(String name) { return of(name, DataType.BOOLEAN); }
    public static Variable<Boolean> ofBoolean(String name, Boolean val) { return of(name, DataType.BOOLEAN, val); }
    public Variable<Boolean> getAsBoolean() { return getAs(DataType.BOOLEAN); }

    // Player
    public static Variable<PlayerEntity> ofPlayer(String name) { return of(name, DataType.PLAYER); }
    public static Variable<PlayerEntity> ofPlayer(String name, PlayerEntity val) { return of(name, DataType.PLAYER, val); }
    public Variable<PlayerEntity> getAsPlayer() { return getAs(DataType.PLAYER); }

    // World
    public static Variable<MpyWorld> ofWorld(String name) { return of(name, DataType.WORLD); }
    public static Variable<MpyWorld> ofWorld(String name, MpyWorld val) { return of(name, DataType.WORLD, val); }
    public Variable<MpyWorld> getAsWorld() { return getAs(DataType.WORLD); }

    // Dimension
    public static Variable<MpyDimension> ofDimension(String name) {return of(name,DataType.DIMENSION);}
    public static Variable<MpyDimension> ofDimension(String name, MpyDimension val) {return of(name, DataType.DIMENSION, val);}
    public Variable<MpyDimension> getAsDimension() { return getAs(DataType.DIMENSION); }

    // Text
    public static Variable<MpyText> ofText(String name) {return of(name,DataType.TEXT);}
    public static Variable<MpyText> ofText(String name, MpyText val) {return of(name, DataType.TEXT, val);}
    public Variable<MpyText> getAsText() { return getAs(DataType.TEXT); }

    public static Variable<MpyStyle> ofStyle(String name) {return of(name,DataType.STYLE);}
    public static Variable<MpyStyle> ofStyle(String name, MpyStyle val) {return of(name, DataType.STYLE, val);}
    public Variable<MpyStyle> getAsStyle() { return getAs(DataType.STYLE); }

    // List
    // List的无参构建，
    //    public static Variable<MpyList> ofList(String name) { return of(name, DataType.LIST); }
    public static Variable<MpyList> ofList(String name, MpyList val) {
        return new Variable<>(name, DataType.LIST.setChild(val.getDataType()), val);
    }
    public Variable<MpyList> getAsList() { return getAs(DataType.LIST); }


    public DataType getDataType() {
        return this.dataType;
    }
    public boolean isSameDataType(DataType dataType) {
        return dataType.isSameTypeAs(dataType);
    }

    public boolean isSameDataType(Variable<?> variable) {
        return dataType.isSameTypeAs(variable.dataType);
    }

    public T getValue() {
        return value;
    }


    @Override
    public String toString() {
        if(value == null) return null;
        if(value instanceof Character c) return Character.toString(c);
        return value.toString();
    }

    public String getName() {
        return this.name;
    }

    // 便捷方法
    public String getString(String e) {
        if(this.isSameDataType(DataType.STRING)) {
            return this.getAsString().getValue();
        } else {
            throw new UnexpectedDataTypeException(e);
        }
    }

    /**
     * 直接设置Value
     * @param value 值
     */
    public void setValue(Object value) {
        this.value = (T) value;
    }

    // 字面double转Float
    public Variable<Float> toFloat() {
        if(isSameDataType(DataType.LITERAL_DOUBLE) || isSameDataType(DataType.DOUBLE)) {
             return ofFloat(name, getAsDouble().getValue().floatValue());
        } else {
            return ofFloat(name, Float.parseFloat(toString()));
        }
    }

    public Variable<Boolean> InvertBoolean() {
        if(this.isSameDataType(DataType.BOOLEAN)) {
            this.getAsBoolean().value = !this.getAsBoolean().getValue();
        }
        return this.getAsBoolean();
    }


    // 计算
    public Variable<?> sum(Variable<?> variable) {
        if(this.dataType.isNumber() && variable.dataType.isNumber()) {
            BigDecimal b1 = new BigDecimal(this.value.toString());
            BigDecimal b2 = new BigDecimal(variable.value.toString());

            return switch (this.dataType) {
                case INT, LITERAL_INTEGER -> ofInteger("%TEMP", b1.add(b2).intValue());
                case FLOAT, LITERAL_FLOAT -> ofFloat("%TEMP", b1.add(b2).floatValue());
                case DOUBLE, LITERAL_DOUBLE -> ofDouble("%TEMP", b1.add(b2).doubleValue());
                default -> throw new RuntimeException("Unknown Error: can't opera:" + b1.doubleValue() + b2.doubleValue());
            };

        } else if(this.dataType.isString() || variable.dataType.isString()) {
            return ofString("%TEMP", this.toString() + variable.toString());
        }
        return VOID();
    }

    public Variable<?> sub(Variable<?> variable) {
        if(this.dataType.isNumber() && variable.dataType.isNumber()) {
            BigDecimal b1 = new BigDecimal(this.value.toString());
            BigDecimal b2 = new BigDecimal(variable.value.toString());

            return switch (this.dataType) {
                case INT, LITERAL_INTEGER -> ofInteger("%TEMP", b1.subtract(b2).intValue());
                case FLOAT, LITERAL_FLOAT -> ofFloat("%TEMP", b1.subtract(b2).floatValue());
                case DOUBLE, LITERAL_DOUBLE -> ofDouble("%TEMP", b1.subtract(b2).doubleValue());
                default -> throw new RuntimeException("Unknown Error: can't opera:" + b1.doubleValue() + b2.doubleValue());
            };
        }
        return VOID();
    }

    public Variable<?> mul(Variable<?> variable) {
        if(this.dataType.isNumber() && variable.dataType.isNumber()) {
            BigDecimal b1 = new BigDecimal(this.value.toString());
            BigDecimal b2 = new BigDecimal(variable.value.toString());

            return switch (this.dataType) {
                case INT, LITERAL_INTEGER -> ofInteger("%TEMP", b1.multiply(b2).intValue());
                case FLOAT, LITERAL_FLOAT -> ofFloat("%TEMP", b1.multiply(b2).floatValue());
                case DOUBLE, LITERAL_DOUBLE -> ofDouble("%TEMP", b1.multiply(b2).doubleValue());
                default -> throw new RuntimeException("Unknown Error: can't opera:" + b1.doubleValue() + b2.doubleValue());
            };
        }
        return VOID();
    }

    public Variable<?> div(Variable<?> variable) {
        if(this.dataType.isNumber() && variable.dataType.isNumber()) {
            BigDecimal b1 = new BigDecimal(this.value.toString());
            BigDecimal b2 = new BigDecimal(variable.value.toString());

            return switch (this.dataType) {
                case INT, LITERAL_INTEGER -> ofInteger("%TEMP", b1.divide(b2).intValue());
                case FLOAT, LITERAL_FLOAT -> ofFloat("%TEMP", b1.divide(b2).floatValue());
                case DOUBLE, LITERAL_DOUBLE -> ofDouble("%TEMP", b1.divide(b2).doubleValue());
                default -> throw new RuntimeException("Unknown Error: can't opera:" + b1.doubleValue() + b2.doubleValue());
            };
        }
        return VOID();
    }

    public int compareTo(Variable<?> variable) {
        if(this.dataType.isNumber() && variable.dataType.isNumber()) {
            BigDecimal b1 = new BigDecimal(this.value.toString());
            BigDecimal b2 = new BigDecimal(variable.value.toString());

            return b1.compareTo(b2);
        }
        return 101;
    }

    public Variable<Boolean> isMoreThan(Variable<?> variable) {
        int result = compareTo(variable);

        return switch (result) {
            case 1 -> ofBoolean("%TEMP",true);
            case 0,-1 -> ofBoolean("%TEMP",false);
            default -> throw new RuntimeException("Can't compare " + this.toString() + " and " + variable.toString());
        };
    }

    public Variable<Boolean> isMoreThanOrEqual(Variable<?> variable) {
        int result = compareTo(variable);

        return switch (result) {
            case 1,0 -> ofBoolean("%TEMP",true);
            case -1 -> ofBoolean("%TEMP",false);
            default -> throw new RuntimeException("Can't compare " + this.toString() + " and " + variable.toString());
        };
    }

    public Variable<Boolean> isLessThan(Variable<?> variable) {
        int result = compareTo(variable);

        return switch (result) {
            case -1 -> ofBoolean("%TEMP",true);
            case 0,1 -> ofBoolean("%TEMP",false);
            default -> throw new RuntimeException("Can't compare " + this.toString() + " and " + variable.toString());
        };
    }

    public Variable<Boolean> isLessThanOrEqual(Variable<?> variable) {
        int result = compareTo(variable);

        return switch (result) {
            case -1,0 -> ofBoolean("%TEMP",true);
            case 1 -> ofBoolean("%TEMP",false);
            default -> throw new RuntimeException("Can't compare " + this.toString() + " and " + variable.toString());
        };
    }

    public Variable<Boolean> isEqual(Variable<?> variable) {
        int result = compareTo(variable);

        if(result == 101) {
            if(!variable.isSameDataType(variable)) {
                if(this.dataType.isString() && variable.dataType.isString()) {
                    return ofBoolean("%TEMP", Objects.equals(this.toString(), variable.toString()));
                }
            } else if(this.isSameDataType(DataType.STRING)) {
                return ofBoolean("%TEMP", Objects.equals(this.toString(), variable.toString()));
            }
        }

        return switch (result) {
            case 0 -> ofBoolean("%TEMP",true);
            case 1,-1 -> ofBoolean("%TEMP",false);
            default -> throw new RuntimeException("Can't compare " + this.toString() + " and " + variable.toString());
        };
    }


    public Variable<?> or(Variable<?> variable) {
        return Variable.ofBoolean("%TEMP", this.getAsBoolean().getValue() || variable.getAsBoolean().getValue());
    }

    public Variable<?> and(Variable<?> variable) {
        return Variable.ofBoolean("%TEMP", this.getAsBoolean().getValue() && variable.getAsBoolean().getValue());
    }

    public double getNumber() {
        return Double.parseDouble(this.value.toString());
    }
}
