package com.ffanxxy.minepyloader.minepy.config;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.config.element.ConfigElement;
import com.ffanxxy.minepyloader.minepy.config.element.ConfigElements;
import com.ffanxxy.minepyloader.minepy.utils.MpyVersion;
import com.ffanxxy.minepyloader.minepy.utils.builder.ConfigElementsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderConfig extends Config {
    public final MpyVersion version;

    public LoaderConfig() {
        super();
        this.version = new MpyVersion(super.elements.getString("version"));
    }

    public LoaderConfig(List<String> lines) {
        super(lines);
        this.version = new MpyVersion(super.elements.getString("version"));
    }

    public static LoaderConfig GlobalConfig = null;

    @Override
    public ConfigElements getDefault() {
        return new ConfigElements(
                ConfigElement.ofString("version", Minepyloader.LOADER_VERSION.toString()),
                ConfigElement.ofBoolean("run_when_initialization", true)
        );
    }

    @Override
    public List<String> build() {
        Map<String,String> annotation = new HashMap<>();
        annotation.put("version","# The version of the loader, it decides what scripts is can run.");
        annotation.put("run_when_initialization","# Whether the initialization script is allowed");
        return builder().build(annotation);
    }
}
