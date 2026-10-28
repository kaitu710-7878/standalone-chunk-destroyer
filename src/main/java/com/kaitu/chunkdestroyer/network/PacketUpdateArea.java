package com.kaitu.chunkdestroyer.network;

import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateArea {
    private final BlockPos pos;
    private final int chunkNorth;
    private final int chunkSouth;
    private final int chunkWest;
    private final int chunkEast;

    public PacketUpdateArea(BlockPos pos, int chunkNorth, int chunkSouth, int chunkWest, int chunkEast) {
        this.pos = pos;
        this.chunkNorth = chunkNorth;
        this.chunkSouth = chunkSouth;
        this.chunkWest = chunkWest;
        this.chunkEast = chunkEast;
    }

    public PacketUpdateArea(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.chunkNorth = buf.readInt();
        this.chunkSouth = buf.readInt();
        this.chunkWest = buf.readInt();
        this.chunkEast = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.chunkNorth);
        buf.writeInt(this.chunkSouth);
        buf.writeInt(this.chunkWest);
        buf.writeInt(this.chunkEast);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.level().isLoaded(pos)) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof ChunkDestroyerBlockEntity destroyer) {
                    destroyer.setChunkRange(chunkNorth, chunkSouth, chunkWest, chunkEast);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
