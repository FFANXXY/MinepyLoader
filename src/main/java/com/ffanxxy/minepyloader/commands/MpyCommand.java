package com.ffanxxy.minepyloader.commands;

import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.scriptObject.Script;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class MpyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access,
                                CommandManager.RegistrationEnvironment environment)
    {
        dispatcher.register(
                CommandManager.literal("methods")
                        .requires(
                                source -> source.hasPermissionLevel(2)
                        )
                        .then(CommandManager.argument("package", StringArgumentType.greedyString())
                                .suggests(
                                        (context, builder) -> {
                                            Minepy.METHODS.forEach(
                                                    method ->  builder.suggest(method.path() + "." + method.name())
                                            );
                                            return builder.buildFuture();
                                        }
                                )
                                .executes(MpyCommand::run)
                        )
        );
    }

    public static int run(CommandContext<ServerCommandSource> ctx) {
        return runMethod(ctx);
    }

    public static int runMethod(CommandContext<ServerCommandSource> ctx) {
        String methodName = StringArgumentType.getString(ctx, "package");

        Minepy.Method method = MethodHelper.saveGetMethod(methodName);

        if (method == null) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("无法找到方法").formatted(Formatting.RED),
                    true
            );
            return 0;
        }
        method.run(new ArrayList<>());

        return 1;
    }
}
