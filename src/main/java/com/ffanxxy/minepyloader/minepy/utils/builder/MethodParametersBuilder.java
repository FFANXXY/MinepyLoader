package com.ffanxxy.minepyloader.minepy.utils.builder;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodParametersBuilder {

    public Map<String, List<DataType>> plans;

    public MethodParametersBuilder() {
        plans = new HashMap<>();
    }

    public void add(String plan, List<DataType> dataTypes) {
        plans.put(plan, dataTypes);
    }

    public void add(String plan, DataType... dataTypes) {
        plans.put(plan, Arrays.stream(dataTypes).toList());
    }

    public Map<String, List<DataType>> getPlans() {
        return plans;
    }
}
