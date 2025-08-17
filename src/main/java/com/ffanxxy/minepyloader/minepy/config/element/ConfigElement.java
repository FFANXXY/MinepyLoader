package com.ffanxxy.minepyloader.minepy.config.element;

import com.ffanxxy.minepyloader.minepy.utils.exception.UnexpectedConfigDataTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigElement<T> {
    private final String id;
    private T value;
    private final Class<T> type;

    private ConfigElement(String id,T value, Class<T> clazz) {
        this.id = id;
        this.value = value;
        this.type = clazz;
    }

    /**
     * 添加字符串元素
     * <p> 若无值，请传入null </p>
     * @param id 相当于key
     * @param value 值
     * @return 新的元素
     */
    public static ConfigElement<String> ofString(String id,@Nullable String value) {
        return new ConfigElement<>(id, value, String.class);
    }

    /**
     * @see #ofString(String, String)
     */
    public static ConfigElement<Boolean> ofBoolean(String id,@Nullable Boolean value) {
        return new ConfigElement<>(id, value, Boolean.class);
    }
    /**
     * @see #ofString(String, String)
     */
    public static @NotNull ConfigElement<Integer> ofInteger(String id, @Nullable Integer value) {
        return new ConfigElement<>(id, value, Integer.class);
    }
    /**
     * @see #ofString(String, String)
     */
    public static ConfigElement<Float> ofFloat(String id,@Nullable Float value) {
        return new ConfigElement<>(id, value, Float.class);
    }

    public String getId() {
        return id;
    }
    public void setValue(T value) {
        // 允许设置null值
        if (value == null) {
            this.value = null;
            return;
        }

        // 运行时类型检查
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Invalid type. Expected: " + type.getSimpleName() +
                            ", Actual: " + value.getClass().getSimpleName()
            );
        }

        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isString() {
        return this.type == String.class;
    }

    public boolean isFloat() {
        return this.type == Float.class;
    }

    public boolean isInteger() {
        return this.type == Integer.class;
    }

    public boolean isBoolean() {
        return this.type == Boolean.class;
    }

    public T getValue() {
        return this.value;
    }

    @SuppressWarnings("unchecked")
    public boolean getBoolean() {
        if(this.type == Boolean.class) {
            return ( (ConfigElement<Boolean>) this).getValueOrDefault(true);
        } else {
            throw new UnexpectedConfigDataTypeException("\"" + this.id + "\"" + " needs Boolean, but \"" + this.type + "\" in fact");
        }
    }

    public T getValueOrDefault(T _default) {
        if(value == null) return _default;
        return this.value;
    }

    public String getAsString() {
        if(value == null) return "null";
        return this.value.toString();
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
