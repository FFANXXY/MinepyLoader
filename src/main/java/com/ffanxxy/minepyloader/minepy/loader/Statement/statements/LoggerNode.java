package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Argument;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LoggerNode {
    public static class log implements RunnableNode {

        Parameter p1;
        Parameter p2;

        public log(List<Parameter> parameters) {
            p1 = parameters.get(0);
            p2 = parameters.get(1);
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            String name = "";
            String con = "";
            if(p1.dataType == DataType.LITERAL_STRING) {
                name = p1.name;
            } else {
                Variable<?> v1 = Minepy.getFromSAN(p1.name, variableMap);
                if(v1.isDataType(DataType.STRING)) {
                    name = v1.getAsString().getValue();
                } else {
                    name = v1.toString();
                }
            }

            if(p2.dataType == DataType.LITERAL_STRING) {
                con = p2.name;
            } else {
                Variable<?> v2 = Minepy.getFromSAN(p2.name, variableMap);
                if(v2.isDataType(DataType.STRING)) {
                    con = v2.getAsString().getValue();
                } else {
                    con = v2.toString();
                }
            }

            Logger logger = LoggerFactory.getLogger(name);
            logger.info(con);
            return Variable.VOID();
        }
    }

    public static class warn implements RunnableNode {

        Parameter p1;
        Parameter p2;

        public warn(List<Parameter> parameters) {
            p1 = parameters.get(0);
            p2 = parameters.get(1);
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            String name = "";
            String con = "";
            if(p1.dataType == DataType.LITERAL_STRING) {
                name = p1.name;
            } else {
                Variable<?> v1 = Minepy.getFromSAN(p1.name, variableMap);
                if(v1.isDataType(DataType.STRING)) {
                    name = v1.getAsString().getValue();
                } else {
                    name = v1.toString();
                }
            }

            if(p2.dataType == DataType.LITERAL_STRING) {
                con = p2.name;
            } else {
                Variable<?> v2 = Minepy.getFromSAN(p2.name, variableMap);
                if(v2.isDataType(DataType.STRING)) {
                    con = v2.getAsString().getValue();
                } else {
                    name = v2.toString();
                }
            }

            Logger logger = LoggerFactory.getLogger(name);
            logger.warn(con);
            return Variable.VOID();
        }
    }

    public static class err implements RunnableNode {

        Parameter p1;
        Parameter p2;

        public err(List<Parameter> parameters) {
            p1 = parameters.get(0);
            p2 = parameters.get(1);
        }

        @Override
        public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
            String name = "";
            String con = "";
            if(p1.dataType == DataType.LITERAL_STRING) {
                name = p1.name;
            } else {
                Variable<?> v1 = Minepy.getFromSAN(p1.name, variableMap);
                if(v1.isDataType(DataType.STRING)) {
                    name = v1.getAsString().getValue();
                } else {
                    name = v1.toString();
                }
            }

            if(p2.dataType == DataType.LITERAL_STRING) {
                con = p2.name;
            } else {
                Variable<?> v2 = Minepy.getFromSAN(p2.name, variableMap);
                if(v2.isDataType(DataType.STRING)) {
                    con = v2.getAsString().getValue();
                } else {
                    name = v2.toString();
                }
            }

            Logger logger = LoggerFactory.getLogger(name);
            logger.error(con);
            return Variable.VOID();
        }
    }
}
