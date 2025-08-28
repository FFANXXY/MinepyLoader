package com.ffanxxy.minepyloader.GameInterface.Entity;

import com.ffanxxy.minepyloader.GameInterface.World.MpyDimension;
import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MpyEntity {
    private final EntityType<?> entityType;
    private final MpyWorld world;

    public MpyEntity(String id, MpyWorld world) {
        this.entityType = EntityType.get(id).get();
        this.world = world;
    }

    public void spawn(MpyDimension.Dimension dimension, BlockPos pos) {
        entityType.spawn(world.getDimension(dimension).getWorld(), pos, SpawnReason.TRIGGERED);
    }
}
