package com.ffanxxy.minepyloader.minepy.loader.Parser;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MethodModifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 方法解析器
 */
public class MethodParser implements Parser<Minepy.MethodDefiner> {

    public final Minepy.MethodDefiner method;
    private final ParameterParser parameterParser;

    public MethodParser(String string) {

        parameterParser = new ParameterParser(string.substring(
                string.indexOf("(") + 1,
                string.lastIndexOf(")")
        ));

        // 截取方法定义
        String methodDefine = string.substring(0, string.indexOf("(")).trim();

        List<String> words = new ArrayList<>(Arrays.stream(methodDefine.split("\\s+")).toList());

        Collections.reverse(words);


        if (words.size() < 2)
            throw new RuntimeException("There are too few middle keywords in the method definition: " + string);
        String name = words.get(0);
        DataType returnDatatype = DataType.fromName(words.get(1));
        AccessModifiers accessModifiers;
        // 是否有访问修饰符
        boolean hasAccessModifier = AccessModifiers.isModifier(words.get(words.size() - 1));
        if (hasAccessModifier) {
            accessModifiers = AccessModifiers.fromName(words.get(words.size() - 1));
        } else {
            accessModifiers = AccessModifiers.DEFAULT;
        }

        // 判断方法修饰符
        words.remove(0);
        words.remove(0);
        words.remove(words.size() - 1);
        List<MethodModifiers> modifiers = new ArrayList<>();

        if (!words.isEmpty()) {
            for (String word : words) {
                modifiers.add(MethodModifiers.fromName(word));
            }
        }

        method = new Minepy.MethodDefiner(
                accessModifiers,
                modifiers,
                name,
                returnDatatype,
                parameterParser.getParameters()
        );
    }

    /**
     * 获得方法
     *
     * @param i 无用参数
     * @return 解析出的方法
     */
    @Override
    public Minepy.MethodDefiner get(int i) {
        return method;
    }

    public ParameterParser getParameterParser() {
        return parameterParser;
    }
}
