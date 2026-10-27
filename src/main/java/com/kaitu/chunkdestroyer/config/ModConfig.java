package com.kaitu.chunkdestroyer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue blocksPerTick;
        public final ForgeConfigSpec.BooleanValue mineBedrock;
        public final ForgeConfigSpec.BooleanValue removeFluids;
        public final ForgeConfigSpec.IntValue maxRadiusBlocks;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            blocksPerTick = builder
                    .comment("Number of blocks mined per tick (Speed upgrade maximum default). Default: 64")
                    .defineInRange("blocksPerTick", 64, 1, 2048);

            mineBedrock = builder
                    .comment("Allow Chunk Destroyer to mine bedrock. Default: false")
                    .define("mineBedrock", false);

            removeFluids = builder
                    .comment("Remove fluids (water, lava) in mining area without dropping entity lag. Default: true")
                    .define("removeFluids", true);

            maxRadiusBlocks = builder
                    .comment("Maximum mining radius in blocks from the machine. Default: 512")
                    .defineInRange("maxRadiusBlocks", 512, 16, 4096);

            builder.pop();
        }
    }
}
