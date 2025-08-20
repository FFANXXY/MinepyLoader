package com.ffanxxy.minepyloader.minepy.loader.Parser;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterParser implements Parser<Parameter> {

    private final List<Parameter> parameters;

    public ParameterParser(String string) {
        String newStr = string.trim();
        if (newStr.isEmpty()) {
            parameters = new ArrayList<>();
            return;
        }
        parameters = Arrays.stream(newStr.split(",")).map(Parameter::new).toList();
    }

    @Override
    public Parameter get(int i) {
        return parameters.get(i);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
