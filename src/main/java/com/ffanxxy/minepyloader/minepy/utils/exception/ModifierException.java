package com.ffanxxy.minepyloader.minepy.utils.exception;

public class ModifierException extends RuntimeException {
    public ModifierException(String message) {
        super("[mpy] " + message);
    }
}
