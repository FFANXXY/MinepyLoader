package com.ffanxxy.minepyloader.minepy.utils.builder;

import com.ffanxxy.minepyloader.minepy.config.element.ConfigElement;
import com.ffanxxy.minepyloader.minepy.config.element.ConfigElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigElementsBuilder {
    private static final Logger log = LoggerFactory.getLogger(ConfigElementsBuilder.class);
    private final ConfigElements elements;

    public ConfigElementsBuilder(ConfigElements configElements) {
        this.elements = configElements;
    }

    public List<String> build() {
        List<String> lines = new ArrayList<>();
        elements.copy().getElements().forEach(
                configElement -> {
                    lines.add(configElement.getId() + " = \"" + configElement.getValue() + "\"");
                }
        );
        return lines;
    }

    /**
     * 对输出的所有元素进行自定义操作
     * @param consumer 进行的操作
     * @return 构建完成的列表
     *
     * <blockquote>
     * <pre>
     *     {@code
     *     build(
     *       (configElement, strings) -> {
     *          strings.add(0, "# 在前方都写上内容，例如：下面的配置是: "
     *          + configElement.getId());
     *         }
     *     );
     *     }
     * </pre>
     * </blockquote>
     */
    public List<String> build(BiConsumer<ConfigElement<?>, List<String>> consumer) {
        List<String> lines = new ArrayList<>();
        elements.copy().getElements().forEach(
                configElement -> {
                    ArrayList<String> temp = new ArrayList<>();
                    if(configElement.getType() == String.class) {
                        temp.add(configElement.getId() + " = \"" + configElement.getValue() + "\"");
                    }else {
                        temp.add(configElement.getId() + " = " + configElement.getValue());
                    }
                    consumer.accept(configElement, temp);
                    lines.addAll(temp);
                }
        );
        return lines;
    }

    public List<String> build(Map<String,String> ano) {
        return build(
                (element, list) -> {
                    if(ano.containsKey(element.getId())) {
                        list.add(0, ano.get(element.getId()));
                    }
                }
        );
    }
}

