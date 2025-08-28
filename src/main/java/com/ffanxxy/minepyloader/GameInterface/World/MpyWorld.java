package com.ffanxxy.minepyloader.GameInterface.World;

import com.ffanxxy.minepyloader.Minepyloader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Map;

public class MpyWorld {
    private ServerWorld OverWorld;
    private ServerWorld Nether;
    private ServerWorld End;

    public MpyWorld() {
        this.OverWorld = Minepyloader.serverInstance.getWorld(ServerWorld.OVERWORLD);
        this.Nether = Minepyloader.serverInstance.getWorld(ServerWorld.NETHER);
        this.End = Minepyloader.serverInstance.getWorld(ServerWorld.END);
    }

    public Map<String, ServerWorld> getWorlds(){
        return Map.of(
                "overworld", OverWorld,
                "nether", Nether,
                "end", End
        );
    }

    public MpyDimension getDimension(MpyDimension.Dimension dimension) {
        return switch (dimension) {
            case OVERWORLD -> dimension.withWorld(OverWorld);
            case NETHER -> dimension.withWorld(Nether);
            case END -> dimension.withWorld(End);
        };
    }

    public static List<ServerPlayerEntity> getPlayers() {
        return Minepyloader.serverInstance.getPlayerManager().getPlayerList();
    }

}
