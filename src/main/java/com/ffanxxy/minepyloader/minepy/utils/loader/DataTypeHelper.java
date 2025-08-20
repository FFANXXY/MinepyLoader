package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

import java.util.Map;

public class DataTypeHelper {
    /**
     * 通过定义上下文，判断形参是否为字面量，如果不，则从定义上下文获得数据类型
     * @param parameter 形参
     * @param defineContext 定义上下文
     * @return 数据类型，将保留字面量类型
     */
    public static DataType getDataType(Parameter parameter, Map<String, DataType> defineContext) {
        if(parameter.dataType.isLiteral()) {
            return parameter.dataType;
        } else {
            return defineContext.get(parameter.name);
        }
    }
}
