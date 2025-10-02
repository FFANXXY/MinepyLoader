package com.ffanxxy.minepyloader.client.Provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModLanguageProvider {
    public static class EnglishProvider extends FabricLanguageProvider {

        public EnglishProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            builder.add("mpy.command.no_exist", "Couldn't find the method");
            builder.add("mpy.command.no_match", "Found some methods, but no one matches the parameters");
            builder.add("mpy.command.return", "Method outputs the return value: %s");
        }
    }

    public static class ChineseProvider extends FabricLanguageProvider {

        public ChineseProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "zh_cn");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            builder.add("mpy.command.no_exist", "找不到方法");
            builder.add("mpy.command.no_match", "找到了方法，可是参数不匹配");
            builder.add("mpy.command.return", "方法输出了返回值: %s");
        }
    }
}
