package com.ffanxxy.minepyloader.client.Provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModLanguageProvider {
    public static class EnglishProvider extends FabricLanguageProvider {

        protected EnglishProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {

        }
    }

    public static class ChineseProvider extends FabricLanguageProvider {

        protected ChineseProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "zh_cn");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {

        }
    }
}
