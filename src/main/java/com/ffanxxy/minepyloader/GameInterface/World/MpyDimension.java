package com.ffanxxy.minepyloader.GameInterface.World;

import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.core.jmx.Server;

public class MpyDimension {
    private ServerWorld world;
    private Dimension dimension;

    public MpyDimension(Dimension dimension, ServerWorld world) {
        this.world = world;
        this.dimension = dimension;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public enum Dimension{
        OVERWORLD,
        NETHER,
        END;

        public MpyDimension withWorld(ServerWorld serverWorld) {
            return new MpyDimension(this, serverWorld);
        }
    }
}
