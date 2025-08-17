package com.ffanxxy.minepyloader.minepy.utils;

import com.ffanxxy.minepyloader.minepy.utils.exception.UnknownValueException;

import java.util.HashMap;
import java.util.Map;

public class MpyVersion {
    private final int runtimeVersion;
    private final String showVersionText;

    public static final Map<Integer, String> VersionTextMap = new HashMap<>();

    static {
        VersionTextMap.put(1, "Demo-0.0.1");
    }

    public MpyVersion(int runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
        this.showVersionText = VersionTextMap.getOrDefault(runtimeVersion, "UnknownVersion");
    }

    public MpyVersion(String showVersionText) {
        this.showVersionText = showVersionText;
        for(int versionInt : VersionTextMap.keySet()) {
            String vt = VersionTextMap.get(versionInt);
            if(showVersionText.equals(vt)) {
                this.runtimeVersion = versionInt;
               return;
            }
        }
        this.runtimeVersion = -1;
        throw new UnknownValueException("MinepyLoader: Unknown Version from \"" + showVersionText + "\"");
    }

    @Override
    public String toString() {
        return this.showVersionText;
    }
}
