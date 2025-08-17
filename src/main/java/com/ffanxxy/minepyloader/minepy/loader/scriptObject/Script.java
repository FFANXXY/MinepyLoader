package com.ffanxxy.minepyloader.minepy.loader.scriptObject;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;

import java.nio.file.Path;
import java.util.List;

public class Script {
    private final Minepy script;

    private final List<String> lines;
    private final Path path;

    private final ScriptPackage scriptPackage;

    public Script(List<String> lines, Path path) {
        this.lines = lines;
        this.path = path;

        this.script = new Minepy(this);
        this.scriptPackage = script.getPackage();
    }

    public void runStatic() {
        script.runStatic();
    }

    public List<String> getLines() {
        return lines;
    }

    public Path getPath() {
        return path;
    }

    public ScriptPackage getScriptPackage() {
        return scriptPackage;
    }
}
