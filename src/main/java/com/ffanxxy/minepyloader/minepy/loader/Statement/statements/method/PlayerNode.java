package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MpyList;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
        builder.add("send",2,DataType.PLAYER,DataType.STRING);
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
                        Text.literal(args.getString(1)),
                        false
                );
                yield Variable.VOID();
            }
            default -> Variable.VOID();
        };
    }
}
