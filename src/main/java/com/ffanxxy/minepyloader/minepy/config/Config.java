package com.ffanxxy.minepyloader.minepy.config;

import com.ffanxxy.minepyloader.minepy.config.element.ConfigElement;
import com.ffanxxy.minepyloader.minepy.config.element.ConfigElements;
import com.ffanxxy.minepyloader.minepy.utils.Results.ConfigLineResult;
import com.ffanxxy.minepyloader.minepy.utils.builder.ConfigElementsBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class Config {
    public final ConfigElements elements;
    public final boolean shouldComplete;

    public Config() {
        elements = getDefault();
        shouldComplete = true;
    }

    public Config(List<String> lines) {
        this.elements = new ConfigElements(create(lines));
        this.shouldComplete = ! elements.includesAll(getDefault());
        elements.completeWith(getDefault());
    }

    public ConfigElements getElements() {
        return elements;
    }

    /**
     * 重写getDefault获得需要的元素
     */
    public abstract ConfigElements getDefault();

    public List<ConfigElement<?>> create(List<String> lines) {
        // 预构建元素组
        List<ConfigElement<?>> elements = new ArrayList<>();
        for(String cfg : lines) {
            ConfigElement<?> element = getConfigElement(cfg);
            if(element == null) continue;
            elements.add(element);
        }

        return elements;
    }

    private ConfigElement<?> getConfigElement(String line) {
        ConfigLineResult readResult = readLine(line);
        if(readResult.isAnnotation() || readResult.isErr() || readResult.getKey() == null) {
            return null;
        }
        if(readResult.getValue() == null) {
            return ConfigElement.ofString(readResult.getKey(), null);
        }

        String key = readResult.getKey();

        // 进行格式分析转换
        if(readResult.getValue().startsWith("\"") && readResult.getValue().endsWith("\"")) {
            String value = readResult.getValue();
            return ConfigElement.ofString(key, value.substring(1,value.length() - 1));

        } else if(readResult.isBoolean()) {
            return ConfigElement.ofBoolean(key, readResult.getValueAsBoolean());

        } else if(readResult.getValueAsInt() != null) {
            return ConfigElement.ofInteger(key, readResult.getValueAsInt());

        } else if(readResult.getValueAsFloat() != null) {
            return ConfigElement.ofFloat(key, readResult.getValueAsFloat());

        } else {

            return ConfigElement.ofString(readResult.getKey(), readResult.getValue());
        }
    }

    private ConfigLineResult readLine(String line) {
        if (line.startsWith("#")) return new ConfigLineResult(true);
        if (line.length() < 2) return new ConfigLineResult();

        int index = line.indexOf("=");
        if(index <= 0) return new ConfigLineResult();
        String key = line.substring(0, index -1).replace(" ","");

        String value = line.substring(index + 1);

        if(value.startsWith(" ")) {
            value = value.substring(1);
        }

        return new ConfigLineResult(key, value);
    }

    public List<String> build() {
        return builder().build();
    }

    public ConfigElementsBuilder builder() {
        return new ConfigElementsBuilder(
            this.elements.copy()
        );
    }
}
