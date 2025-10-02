package com.ffanxxy.minepyloader.minepy.loader.Parser;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.MethodModifier;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.MethodModifiers;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.ParserModifier;
import com.ffanxxy.minepyloader.minepy.utils.loader.PackageGetter;

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


    public MethodParser(String string, List<String> imports) {

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

        // 处理修饰符
        List<MethodModifier> modifiers = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if(i < 2) continue;
            var fullName = PackageGetter.getNameInImport(words.get(i),imports);

            modifiers.add(MethodModifiers.get(fullName));

        }
        if(modifiers.isEmpty()) {
            modifiers.add(MethodModifiers.DEFAULT);
        }

        method = new Minepy.MethodDefiner(
                modifiers,
                name,
                returnDatatype,
                new ArrayList<>(parameterParser.getParameters()),
                new ArrayList<>(parameterParser.getParameters())
        );

        List<ParserModifier> parserModifiers = new ArrayList<>();

        // 修饰符检测
        modifiers.forEach(methodModifier -> {
            if(methodModifier instanceof ParserModifier parserModifier) {
                parserModifiers.add(parserModifier);
            }
        });

        if(parserModifiers.isEmpty()) return;

        for (int i = 0; i < parserModifiers.size(); i++) {
            parserModifiers.get(i).modify(method, i);
            this.method.parameters().remove(0);
        }
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
