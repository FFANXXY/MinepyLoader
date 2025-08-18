package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.AccessModifiers;

/**
 * 全局变量的作用域和索引
 */
public class AccessAndIndex {

    public AccessModifiers accessModifier;
    public ScriptPackage path;

    public AccessAndIndex(AccessModifiers modifier, ScriptPackage path , String index) {
        this.accessModifier = modifier;
        this.path = path;
    }
}
