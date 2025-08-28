package com.ffanxxy.minepyloader.commands;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Method;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Parser.ParameterParser;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.utils.loader.MethodHelper;
import com.ffanxxy.minepyloader.minepy.utils.loader.ValueGetter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                        .then(CommandManager.argument("package", StringArgumentType.string())
                                .then(CommandManager.argument("paras", StringArgumentType.greedyString())
                                        .executes(MpyCommand::runWith)
                                )
                                .suggests(
                                        (context, builder) -> {
                                            Minepy.METHODS.forEach(
                                                    method ->  builder.suggest(method.getPath() + "." + method.getName())
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

    public static int runWith(CommandContext<ServerCommandSource> ctx) {
        return runMethodWith(ctx);
    }

    public static int runMethod(CommandContext<ServerCommandSource> ctx) {
        String methodName = StringArgumentType.getString(ctx, "package");

        Method method = MethodHelper.saveGetMethod(methodName);

        if (method == null) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("无法找到方法").formatted(Formatting.RED),
                    true
            );
            return 0;
        }
        CompletableFuture<Variable<?>> future = method.run(new HashMap<>());

        try {
            Variable<?> variable = future.get();
            if(!variable.isVoid()) {
                ctx.getSource().sendFeedback(
                        () -> Text.literal("方法输出了返回值: " + variable).formatted(Formatting.GRAY),
                        true
                );
            }

            return 1;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int runMethodWith(CommandContext<ServerCommandSource> ctx) {
        String methodName = StringArgumentType.getString(ctx, "package");
        String paras = StringArgumentType.getString(ctx,"paras");

        var vars = ValueGetter.getArguments(paras);

        Method method = MethodHelper.saveGetMethodFromVar(methodName, vars);

        if (method == null) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("无法找到方法").formatted(Formatting.RED),
                    true
            );
            return 0;
        }

        Map<Minepy.ScopeAndName, Variable<?>> resultRunArgs = getRunArgs(vars, method);

        if(resultRunArgs == null) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("找到了方法，可是参数不匹配").formatted(Formatting.RED),
                    true
            );
            return 0;
        }

        var future = method.run(resultRunArgs);

        try {
            Variable<?> variable = future.get();

            if(!variable.isVoid()) {
                ctx.getSource().sendFeedback(
                        () -> Text.literal("方法输出了返回值: " + variable.toString()).formatted(Formatting.GRAY),
                        true
                );
            }
            return 1;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable Map<Minepy.ScopeAndName, Variable<?>> getRunArgs(List<Variable<?>> vars, Method method) {
        Map< Minepy.ScopeAndName, Variable<?>> resultRunArgs = new HashMap<>();

        // 对输入的参数进行解析
        for (int i = 0; i < vars.size(); i++) {
            var methodParameter = method.getParameters().get(i);
            var inVar= vars.get(i);

            if (!methodParameter.dataType.isSameTypeAs(inVar.getDataType())) return null;

            resultRunArgs.put(
                    new Minepy.ScopeAndName(0, methodParameter.name),
                    inVar
            );
        }
        return resultRunArgs;
    }
}
