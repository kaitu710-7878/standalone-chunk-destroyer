package com.kaitu.chunkdestroyer.network;

import com.kaitu.chunkdestroyer.ChunkDestroyerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ChunkDestroyerMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++,
                PacketUpdateArea.class,
                PacketUpdateArea::toBytes,
                PacketUpdateArea::new,
                PacketUpdateArea::handle
        );

        CHANNEL.registerMessage(packetId++,
                PacketToggleState.class,
                PacketToggleState::toBytes,
                PacketToggleState::new,
                PacketToggleState::handle
        );
    }
}
