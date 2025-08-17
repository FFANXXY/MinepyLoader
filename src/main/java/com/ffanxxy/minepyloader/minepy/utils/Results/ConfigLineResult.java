package com.ffanxxy.minepyloader.minepy.utils.Results;

import org.jetbrains.annotations.Nullable;

public class ConfigLineResult {

    private boolean err;
    private boolean isAnnotation;

    private String key;
    private String value;

    public ConfigLineResult(
            String key,
            String Value
    ) {
        this.err = false;
        this.key = key;
        this.value = Value;
    }

    public ConfigLineResult() {
        this.err = true;
    }

    public ConfigLineResult(
            boolean isAnnotation
    ) {
        this.isAnnotation = isAnnotation;
    }

    public @Nullable String getKey() {
        if(isAnnotation || err) {
            return null;
        }
        return key;
    }

    public @Nullable String getValue() {
        if(isAnnotation || err) {
            return null;
        }
        return value;
    }

    /**
     * 尝试将值作为整数读取
     * @return 读取的值，如果无法解析，返回为null
     */
    public @Nullable Integer getValueAsInt() {
        if(isAnnotation || err) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public @Nullable Float getValueAsFloat() {
        if(isAnnotation || err || value == null) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 尝试将值作为布尔值读取
     * @return 读取的值，如果无法读取或出现错误，则返回null
     * @see Boolean#getBoolean(String)
     */
    public @Nullable Boolean getValueAsBoolean() {
        if(isAnnotation || err) {
            return null;
        }
        try{
            return Boolean.parseBoolean(value);
        } catch (SecurityException e) {
            return null;
        }
    }

    public boolean isBoolean() {
        return switch (value) {
            case "true","false" -> true;
            default -> false;
        };
    }

    public boolean isAnnotation() {
        return isAnnotation;
    }

    public boolean isErr() {
        return err;
    }
}
