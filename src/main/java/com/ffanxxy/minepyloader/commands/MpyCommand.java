package com.ffanxxy.minepyloader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class MpyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access,
                                CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(
                CommandManager.literal("minepy")
                        .then(CommandManager.argument("path", StringArgumentType.greedyString()))
                        .executes(MpyCommand::run)
        );
    }

    public static int run(CommandContext<ServerCommandSource> ctx) {
        return run();
    }

    public static int run() {
        return 1;
    }
}
