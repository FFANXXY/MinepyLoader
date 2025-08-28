package com.ffanxxy.minepyloader.minepy.utils.loader;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Method;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodHelper {
    /**
     * 自推断方法
     * @param pathAndName 方法完整路径
     * @param dataTypes 输入的参数数据类型
     * @return 方法
     */
    public static @NotNull Method getMethod(String pathAndName, List<DataType> dataTypes) {
        // 获得变量
        List<Method> methods = Minepy.METHODS.stream().filter(
                m -> (m.getPath() + "." + m.getName()).equals(pathAndName) //是否为调用的方法
        ).toList();

        if(methods.isEmpty()) throw new RuntimeException("Unknown method:" + pathAndName);

        // 具体方法判断
        // 允许方法同名，模拟重写
        if(methods.size() == 1) {
            return methods.get(0);
        } else {
            // 获得精确列表

            List<Method> detailMethods;
            if(dataTypes.isEmpty()) {
                detailMethods = methods.stream()
                        .filter(
                                m1 -> m1.getParameters().isEmpty()
                        ).toList();
                if(!detailMethods.isEmpty()) return detailMethods.get(0);
                else throw new RuntimeException("There are too many methods, what it really represent: " + pathAndName);
            } else {
                // 精确判断
                detailMethods = methods.stream()
                        .filter(
                                m1 -> m1.getParameters().size() == dataTypes.size()
                        ).toList();
                for(Method method : detailMethods) {
                    boolean isSameDataType = true;
                    for (int i = 0; i < method.getParameters().size(); i++) {
                        Parameter methodParameter = method.getParameters().get(i);
                        DataType inputDataType = dataTypes.get(i);

                        isSameDataType = isSameDataType && methodParameter.dataType.isSameTypeAs(inputDataType);
                    }
                    if(isSameDataType) {
                       return method;
                    }
                }
                // 循环结束
                throw new RuntimeException("There are no known methods that meet the parameters: " + pathAndName);
            }
        }
    }

    /**
     * 通过变量的一组类型获得方法
     * @see #saveGetMethod(String, List)
     */
    public static @Nullable Method saveGetMethodFromVar(String pathAndName, List<Variable<?>> variables) {
        List<DataType> datatypes = new ArrayList<>();
        variables.forEach(
                variable ->  datatypes.add(variable.getDataType())
        );
        return saveGetMethod(pathAndName, datatypes);
    }

    public static @NotNull Method getMethodFromVar(String pathAndName, List<Variable<?>> variables) {
        List<DataType> datatypes = new ArrayList<>();
        variables.forEach(
                variable ->  datatypes.add(variable.getDataType())
        );
        return getMethod(pathAndName, datatypes);
    }

    /**
     * 安全地自推断方法，不会有报错，无法找到时会返回nullable
     * @param pathAndName 方法完整路径
     * @param dataTypes 方法参数
     * @return 方法
     */
    public static @Nullable Method saveGetMethod(String pathAndName, List<DataType> dataTypes) {
        try {
            return getMethod(pathAndName,dataTypes);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 默认参数为0的方法获得
     * @param name 方法名
     * @return 方法
     * @see #getMethod(String, List)
     */
    public static @NotNull Method getMethod(String name) {
        return getMethod(name , new ArrayList<>());
    }

    public static @Nullable Method saveGetMethod(String name) {
        return saveGetMethod(name , new ArrayList<>());
    }

    /**
     * 解析导入的方法
     * @param list 导入组
     * @return 方法组
     */
    public static List<Method> parserImports(List<String> list) {
        var methods = Minepy.METHODS;
        List<Method> resultMethod = new ArrayList<>();

        for(String i : list) {
            if(i.trim().endsWith("*")) {
                resultMethod.addAll(
                        methods.stream().filter(
                                m -> m.getPath().isSamePackage(i)
                        ).toList()
                );
            } else {
                resultMethod.addAll(
                        methods.stream().filter(
                                m -> Objects.equals(m.getPath() + "." + m.getName(), i.trim())
                        ).toList()
                );
            }
        }

        return resultMethod;
    }

    /**
     * 从导入获得方法
     *
     * @param list 导入列表
     * @param name 方法名
     * @param parameters 形参
     * @return 方法
     */
    public static Method getMethodFromImports(List<String> list, String name, List<Parameter> parameters) {
        var Methods = parserImports(list);

        // 获得变量
        List<Method> methods = Methods.stream().filter(
                m -> m.getName().equals(name) //是否为调用的方法
        ).toList();

        if(methods.isEmpty()) throw new RuntimeException("Unknown method:" + name);

        Method mtd = null;

        // 具体方法判断
        // 允许方法同名，模拟重写
        if(methods.size() == 1) {
            mtd = methods.get(0);
        } else {
            // 获得精确列表

            List<Method> detailMethods;
            if(parameters.isEmpty()) {
                detailMethods = methods.stream()
                        .filter(
                                m1 -> m1.getParameters().isEmpty()
                        ).toList();
            } else {
                // 精确判断
                detailMethods = methods.stream()
                        .filter(
                                m1 -> m1.getParameters().size() == parameters.size()
                        ).filter(
                                m1 -> m1.getParameters().stream().allMatch(
                                        // 参数不应该相同，因此直接判断
                                        p -> parameters.get(methods.indexOf(m1)).dataType.isSameTypeAs(p.dataType)
                                )
                        ).toList();
            }

            if(detailMethods.isEmpty()) throw new RuntimeException("There are no known methods that meet the parameters: " + name);
            if(detailMethods.size() > 1) throw new RuntimeException("Surprising err: too more methods has same parameters: " + name);

            mtd = detailMethods.get(0);
        }
        return mtd;
    }

    /**
     * 获得方法的全名，其格式为{@code package.method(Type, Type)}，其需要接受一个方法作为参数。
     * @param method 方法
     * @return 全名
     */
    public static String getMethodFullName(Method method) {
        StringBuilder paras = new StringBuilder();
        var paraList = method.getParameters();

        for (int i = 0; i < paraList.size(); i++) {
            paras.append(paraList.get(i).dataType.getName());
            if (i != paraList.size() - 1) {
                paras.append(", ");
            }
        }
        return method.getPath() + "." + method.getName() + "(" + paras + ")";
    }
}
