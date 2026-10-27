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
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    public PacketUpdateArea(BlockPos pos, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.pos = pos;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public PacketUpdateArea(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.minX = buf.readInt();
        this.maxX = buf.readInt();
        this.minY = buf.readInt();
        this.maxY = buf.readInt();
        this.minZ = buf.readInt();
        this.maxZ = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.minX);
        buf.writeInt(this.maxX);
        buf.writeInt(this.minY);
        buf.writeInt(this.maxY);
        buf.writeInt(this.minZ);
        buf.writeInt(this.maxZ);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.level().isLoaded(pos)) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof ChunkDestroyerBlockEntity destroyer) {
                    destroyer.setMiningArea(minX, maxX, minY, maxY, minZ, maxZ);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
