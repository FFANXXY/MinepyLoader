package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedDataTypeException;

import java.util.HashMap;
import java.util.Map;

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
    public static Variable<?> createWithNewName(String name, Variable<?> var) {
        return new Variable<>(name, var.dataType, var.value);
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

    //Integer
    public static Variable<Integer> ofInteger(String name) {
        return new Variable<>(name, DataType.STRING);
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
        return new Variable<>(name, DataType.STRING);
    }
    public static Variable<Float> ofFloat(String name,Float  val) {
        return new Variable<>(name,DataType.FLOAT,val);
    }
    public Variable<Float> getAsFloat() {
        if(this.dataType == DataType.FLOAT) return (Variable<Float>) this;
        throw new UnexpectedDataTypeException();
    }



    public DataType getDataType() {
        return this.dataType;
    }
    public boolean isDataType(DataType dataType) {
        return this.dataType == dataType;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getName() {
        return this.name;
    }

    // 便捷方法
    public String getString(String e) {
        if(this.isDataType(DataType.STRING)) {
            return this.getAsString().getValue();
        } else {
            throw new UnexpectedDataTypeException(e);
        }
    }
}
