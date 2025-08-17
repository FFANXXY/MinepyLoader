package com.ffanxxy.minepyloader.minepy.config.element;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigElements {
    public final List<ConfigElement<?>> elements;

    public ConfigElements(ConfigElement<?>... configElements) {
        elements = new ArrayList<>();
       if(configElements.length == 0) return;
       List<String> ids = new ArrayList<>();
       for(ConfigElement<?> element : configElements) {
           if(ids.contains(element.getId())) continue;
           ids.add(element.getId());
           elements.add(element);
       }
    }

    public ConfigElements(List<ConfigElement<?>> configElements) {
        elements = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for(ConfigElement<?> element : configElements) {
            if(ids.contains(element.getId())) continue;
            ids.add(element.getId());
            elements.add(element);
        }
    }

    public boolean contains(String id) {
        return elements.stream().anyMatch(
                element -> Objects.equals(element.getId(), id)
        );
    }

    public boolean contains(ConfigElement<?> ele) {
        return elements.stream().anyMatch(
                element -> Objects.equals(element.getId(), ele.getId())
        );
    }

    public @Nullable ConfigElement<?> get(String id) {
        if(!contains(id)) return null;
        return elements.stream().filter(
                element -> Objects.equals(element.getId(), id)
        ).toList().get(0);
    }

    public @Nullable String getString(String id) {
        if(!contains(id)) return null;
        return elements.stream().filter(
                element -> Objects.equals(element.getId(), id)
        ).toList().get(0).toString();
    }

    /**
     * 直接返回所有元素
     * @return 原始元素
     */
    public List<ConfigElement<?>> getElements() {
        return this.elements;
    }

    public ConfigElements copy() {
        return new ConfigElements(new ArrayList<>(elements));
    }

    /**
     * 与另一个元素组对照，并补全没有的
     * @param another 另一个元素组（不会修改）
     * @return 修改后的该元素组
     */
    public ConfigElements completeWith(ConfigElements another) {
        for(ConfigElement<?> element : another.getElements()) {
            if(!this.contains(element)) {
                this.elements.add(element);
            }
        }
        return this;
    }

    public boolean includesAll(ConfigElements another) {
        boolean res = true;
        for(ConfigElement<?> element : another.getElements()) {
            res = res && this.contains(element);
        }
        return res;
    }
}
