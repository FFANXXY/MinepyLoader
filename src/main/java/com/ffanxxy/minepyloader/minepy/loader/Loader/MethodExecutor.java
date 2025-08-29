package com.ffanxxy.minepyloader.minepy.loader.Loader;

public class MethodExecutor {
    private String msg;
    private final ExecutorType type;

    public MethodExecutor(ExecutorType type, String msg) {
        this.msg = msg;
        this.type = type;
    }

    public MethodExecutor(ExecutorType type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public ExecutorType getType() {
        return type;
    }

    public enum ExecutorType{
        COMMAND,
        STATEMENT
    }
}
