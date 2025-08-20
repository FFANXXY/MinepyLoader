package com.ffanxxy.minepyloader;

import com.ffanxxy.minepyloader.commands.MpyCommand;
import com.ffanxxy.minepyloader.io.Mpyio;
import com.ffanxxy.minepyloader.minepy.config.LoaderConfig;
import com.ffanxxy.minepyloader.minepy.loader.scriptObject.Scripts;
import com.ffanxxy.minepyloader.minepy.utils.MpyVersion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Minepyloader implements ModInitializer {

    public static final String MOD_ID = "minepyloader";

    public static final Logger LOGGER = LoggerFactory.getLogger("MinepyLoader");

    public static final MpyVersion LOADER_VERSION = new MpyVersion(1);

    public static MinecraftServer serverInstance;

    @Override
    public void onInitialize() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("minepyloader");
        Path scriptsPath = FabricLoader.getInstance().getConfigDir().resolve("minepyloader").resolve("scripts");
        // 获取文件内容
        List<String> configs = Mpyio.readOrCreate(configPath.resolve("loader.cfg"), new LoaderConfig().build());
        // 获得结果
        LoaderConfig.GlobalConfig = new LoaderConfig(configs);

        if(LoaderConfig.GlobalConfig.shouldComplete) {
            Mpyio.writeAndReplace(configPath.resolve("loader.cfg"), LoaderConfig.GlobalConfig.build());
        }

        LOGGER.info("Minepy Loader Version: {}", LoaderConfig.GlobalConfig.version.toString());
        boolean run_when_initialization = LoaderConfig.GlobalConfig.getElements().get("run_when_initialization").getBoolean();

        // 生成脚本目录
        Mpyio.createDir(scriptsPath);
        // 读取脚本
        List<File> scriptsFiles = new ArrayList<>();
        Mpyio.getAllFiles(scriptsPath, scriptsFiles);

        scriptsFiles = scriptsFiles.stream().filter(
                file -> FilenameUtils.getExtension(file.getName()).equals("mpy")
        ).toList();

        Scripts scripts = new Scripts(scriptsFiles);

        /*
         * 是否初始化时需要执行
         * 默认运行，运行时请调用runAllStatic，或者写上基本参数
         */
        if(run_when_initialization) {
            LOGGER.info("initialize all code:");
            scripts.runAllStatic();
        }

        // 注册命令
        CommandRegistrationCallback.EVENT.register(MpyCommand::register);
        ServerLifecycleEvents.SERVER_STARTED.register(
                minecraftServer -> serverInstance = minecraftServer
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            serverInstance = null;
        });
    }
}
