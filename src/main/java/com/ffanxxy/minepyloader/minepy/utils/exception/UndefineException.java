package com.ffanxxy.minepyloader.minepy.utils.exception;

public class UndefineException extends RuntimeException {
    public UndefineException(String ObjectType, String ObjectName) {
        super("[mpy indide] Undefine Object: " + ObjectType + " : " + ObjectName);
    }
}
