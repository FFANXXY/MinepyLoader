package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
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
    public static Variable<String> ofString(String name) {
        return new Variable<>(name, DataType.STRING);
    }
    public static Variable<String> ofString(String name, String val) {
        return new Variable<>(name,DataType.STRING, val);
    }
    public Variable<String> getAsString() {
        if(this.dataType == DataType.STRING) return (Variable<String>) this;
        throw new UnexpectedDataTypeException();
    }

    //Char
    public static Variable<Character> ofChar(String name) {
        return new Variable<>(name, DataType.CHAR);
    }
    public static Variable<Character> ofChar(String name, Character val) {
        return new Variable<>(name,DataType.CHAR, val);
    }
    public Variable<Character> getAsChar() {
        if(this.dataType == DataType.CHAR) return (Variable<Character>) this;
        throw new UnexpectedDataTypeException();
    }

    //Integer
    public static Variable<Integer> ofInteger(String name) {
        return new Variable<>(name, DataType.INT);
    }
    public static Variable<Integer> ofInteger(String name,Integer  val) {
        return new Variable<>(name,DataType.INT,val);
    }
    public Variable<Integer> getAsInt() {
        if(this.dataType == DataType.INT) return (Variable<Integer>) this;
        throw new UnexpectedDataTypeException();
    }

    //Float
    public static Variable<Float> ofFloat(String name) {
        return new Variable<>(name, DataType.FLOAT);
    }
    public static Variable<Float> ofFloat(String name,Float  val) {
        return new Variable<>(name,DataType.FLOAT,val);
    }
    public Variable<Float> getAsFloat() {
        if(this.dataType == DataType.FLOAT) return (Variable<Float>) this;
        throw new UnexpectedDataTypeException();
    }

    //Double
    public static Variable<Double> ofDouble(String name) {
        return new Variable<>(name, DataType.DOUBLE);
    }
    public static Variable<Double> ofDouble(String name,Double  val) {
        return new Variable<>(name,DataType.DOUBLE,val);
    }
    public Variable<Double> getAsDouble() {
        if(this.dataType == DataType.DOUBLE) return (Variable<Double>) this;
        throw new UnexpectedDataTypeException();
    }

    //Boolean
    public static Variable<Boolean> ofBoolean(String name) {
        return new Variable<>(name, DataType.BOOLEAN);
    }
    public static Variable<Boolean> ofBoolean(String name,Boolean  val) {
        return new Variable<>(name,DataType.BOOLEAN,val);
    }
    public Variable<Boolean> getAsBoolean() {
        if(this.dataType == DataType.BOOLEAN) return (Variable<Boolean>) this;
        throw new UnexpectedDataTypeException();
    }

    // Player
    public static Variable<PlayerEntity> ofPlayer(String name) {
        return new Variable<>(name, DataType.PLAYER);
    }
    public static Variable<PlayerEntity> ofPlayer(String name,PlayerEntity  val) {
        return new Variable<>(name,DataType.PLAYER,val);
    }
    public Variable<PlayerEntity> getAsPlayer() {
        if(this.dataType == DataType.PLAYER) return (Variable<PlayerEntity>) this;
        throw new UnexpectedDataTypeException();
    }

    // World
    public static Variable<World> ofWorld(String name) {
        return new Variable<>(name, DataType.WORLD);
    }
    public static Variable<World> ofWorld(String name, World  val) {
        return new Variable<>(name,DataType.WORLD,val);
    }
    public Variable<World> getAsWorld() {
        if(this.dataType == DataType.WORLD) return (Variable<World>) this;
        throw new UnexpectedDataTypeException();
    }



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
