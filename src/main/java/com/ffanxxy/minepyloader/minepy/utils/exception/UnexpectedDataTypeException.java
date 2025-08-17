package com.ffanxxy.minepyloader.minepy.utils.exception;

public class UnexpectedDataTypeException extends RuntimeException{
    public UnexpectedDataTypeException() {
        super("It may cause by Internal Errors, please report the error");
    }

    public UnexpectedDataTypeException(String msg) {
        super(msg);
    }
}
