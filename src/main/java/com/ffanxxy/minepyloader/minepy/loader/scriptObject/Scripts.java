package com.ffanxxy.minepyloader.minepy.loader.scriptObject;

import com.ffanxxy.minepyloader.io.Mpyio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scripts {

    public List<Script> scripts = new ArrayList<>();

    public Scripts(List<File> fileList) {
        for (File file : fileList) {
            scripts.add(
                    new Script(Mpyio.read(file.toPath()),file.toPath())
            );
        }
    }

    public void runAllStatic() {
        scripts.forEach(Script::runStatic);
    }
}
