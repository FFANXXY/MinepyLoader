package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import com.ffanxxy.minepyloader.Minepyloader;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MpyList;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerNode extends MethodsNode {
    public PlayerNode(List<VarGetterNode> inputParameters, ScriptParserLineContext context, String method) {
        super(inputParameters, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("all", 0);
        builder.add("kill", 1, DataType.PLAYER);
        builder.add("send",2,DataType.PLAYER,DataType.TEXT);
        builder.add("run",3, DataType.PLAYER, DataType.STRING);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0 -> {
                List<Variable<?>> playerVars = new ArrayList<>();
                for(ServerPlayerEntity serverPlayerEntity : MpyWorld.getPlayers()) {
                    playerVars.add(Variable.ofPlayer("%TEMP", serverPlayerEntity));
                }
                yield Variable.ofList("%TEMP", new MpyList(DataType.PLAYER, playerVars));
            }
            case 1 -> {
                PlayerEntity player = args.getPlayer(0);
                if(player == null) yield Variable.VOID();
                player.kill();
                yield Variable.VOID();
            }
            case 2 -> {
                PlayerEntity player = args.getPlayer(0);
                if(player == null) yield Variable.VOID();
                player.sendMessage(
                        args.getText(1).build(),
                        false
                );
                yield Variable.VOID();
            }
            case 3 -> {
                PlayerEntity player = args.getPlayer(0);
                if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                    ServerCommandSource customSource = new ServerCommandSource(
                            Minepyloader.serverInstance, // 服务器实例
                            serverPlayerEntity.getPos(), // 使用实体的位置作为命令执行位置
                            serverPlayerEntity.getRotationClient(), // 使用实体的朝向
                            (ServerWorld) serverPlayerEntity.getWorld(), // 维度
                            4, // 权限等级
                            serverPlayerEntity.getEntityName(), // 命令源名称（实体的名称）
                            serverPlayerEntity.getName(), // 显示名称
                            Minepyloader.serverInstance, // 游戏规则
                            serverPlayerEntity
                    );

                    yield Variable.ofInteger("%TEMP",
                            Minepyloader.serverInstance.getCommandManager().executeWithPrefix(customSource, args.getString(1)));
                }
                yield Variable.ofInteger("%TEMP", 1);
            }
            default -> Variable.VOID();
        };
    }
}
