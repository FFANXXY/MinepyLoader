package com.ffanxxy.minepyloader.minepy.loader.Statement.Variable;

import com.ffanxxy.minepyloader.minepy.loader.ScriptPackage;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.AccessModifier;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.MethodModifier;

/**
 * 全局变量的作用域和索引
 */
public class AccessAndIndex {

    public AccessModifier accessModifier;
    public ScriptPackage path;

    public AccessAndIndex(AccessModifier modifier, ScriptPackage path , String index) {
        this.accessModifier = modifier;
        this.path = path;
    }
}
