package com.kaitu.chunkdestroyer.network;

import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleState {
    public static final byte ACTION_TOGGLE_WORKING = 0;
    public static final byte ACTION_TOGGLE_SILK = 1;
    public static final byte ACTION_TOGGLE_LIQUIDS = 2;
    public static final byte ACTION_RESET_POS = 3;

    private final BlockPos pos;
    private final byte action;

    public PacketToggleState(BlockPos pos, byte action) {
        this.pos = pos;
        this.action = action;
    }

    public PacketToggleState(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.action = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.action);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.level().isLoaded(pos)) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof ChunkDestroyerBlockEntity destroyer) {
                    switch (action) {
                        case ACTION_TOGGLE_WORKING -> destroyer.toggleWorking();
                        case ACTION_TOGGLE_SILK -> destroyer.toggleSilkTouch();
                        case ACTION_TOGGLE_LIQUIDS -> destroyer.toggleLiquids();
                        case ACTION_RESET_POS -> destroyer.resetMiningCursor();
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
