package com.ffanxxy.minepyloader.client;

import com.ffanxxy.minepyloader.client.Provider.ModLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MinepyloaderDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModLanguageProvider.EnglishProvider::new);
        pack.addProvider(ModLanguageProvider.ChineseProvider::new);
    }
}
