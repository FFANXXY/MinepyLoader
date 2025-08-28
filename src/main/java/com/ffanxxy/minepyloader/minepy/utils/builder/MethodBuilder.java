package com.ffanxxy.minepyloader.minepy.utils.builder;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodBuilder {

    public Map<MethodIndex, Integer> plans;

    public MethodBuilder() {
        plans = new HashMap<>();
    }

    public void add(String name, int index, List<DataType> argumentsDataTypes) {
        plans.put(new MethodIndex(name, argumentsDataTypes), index);
    }

    public void add(String name, int index, DataType... argumentsDataTypes) {
        plans.put(new MethodIndex(name, Arrays.stream(argumentsDataTypes).toList()), index);
    }

    public List<MethodIndex> getPlans() {
        return plans.keySet().stream().toList();
    }

    public record MethodIndex(String name, List<DataType> dataTypes) {

    };
}
