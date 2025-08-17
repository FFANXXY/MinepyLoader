package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;

public class Parameter {

    public final DataType dataType;
    public final String name;

    public Parameter(String s) {
        String newStr = s.trim();

        String[] two = newStr.split("\\s+");

        String dataTypeString = two[0].trim();
        String nameString = two[1].trim();

        /*
            跳过泛型检测
         */
        dataType = DataType.fromName(dataTypeString);
        name = nameString;
    }

    public Parameter(DataType type, String s) {
        this.dataType = type;
        this.name = s;
    }
}
