package com.ffanxxy.minepyloader.minepy.loader.Statement.type.accessModifiers.context;

import com.ffanxxy.minepyloader.minepy.loader.Loader.MethodExecutor;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;

public record ModifierInputContext(
        MethodExecutor executor,
        PackageStructure thisPackage
) {
}
