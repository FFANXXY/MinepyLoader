package com.ffanxxy.minepyloader.GameInterface.World;

import net.minecraft.server.world.ServerWorld;

public record MpyDimension(com.ffanxxy.minepyloader.GameInterface.World.MpyDimension.Dimension dimension,
                           ServerWorld world) {

    public enum Dimension {
        OVERWORLD,
        NETHER,
        END;

        public MpyDimension withWorld(ServerWorld serverWorld) {
            return new MpyDimension(this, serverWorld);
        }
    }
}
