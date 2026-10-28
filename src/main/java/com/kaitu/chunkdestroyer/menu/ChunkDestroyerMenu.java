package com.kaitu.chunkdestroyer.menu;

import com.kaitu.chunkdestroyer.ChunkDestroyerMod;
import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ChunkDestroyerMenu extends AbstractContainerMenu {
    private final ChunkDestroyerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    public ChunkDestroyerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10));
    }

    public ChunkDestroyerMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ChunkDestroyerMod.CHUNK_DESTROYER_MENU.get(), containerId);
        checkContainerSize(playerInventory, 36);
        this.blockEntity = (ChunkDestroyerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.data = data;

        // 内部バッファスロット (上部右側にコンパクトな3スロットまたは1行、または安全用スロット)
        // ここでは安全にアイテムを取り出せるよう、9スロット (1行x9列) を Y=106 に配置
        for (int col = 0; col < 9; col++) {
            this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), col, 8 + col * 18, 106));
        }

        // プレイヤーインベントリ (3行 x 9列) Y=128
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 128 + row * 18));
            }
        }

        // プレイヤーホットバー (9スロット) Y=186
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 186));
        }

        addDataSlots(data);
    }

    public ChunkDestroyerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean isWorking() {
        return this.data.get(0) != 0;
    }

    public boolean isSilkTouch() {
        return this.data.get(1) != 0;
    }

    public boolean isMineLiquids() {
        return this.data.get(2) != 0;
    }

    public boolean isVoidStone() {
        return this.data.get(3) != 0;
    }

    public boolean isVoidDirt() {
        return this.data.get(4) != 0;
    }

    public int getChunkNorth() {
        return this.data.get(5);
    }

    public int getChunkSouth() {
        return this.data.get(6);
    }

    public int getChunkWest() {
        return this.data.get(7);
    }

    public int getChunkEast() {
        return this.data.get(8);
    }

    public int getProgress() {
        return this.data.get(9);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        // 0..8: BlockEntity slots
        // 9..35: Player main inventory
        // 36..44: Player hotbar
        if (index < 9) {
            if (!this.moveItemStackTo(sourceStack, 9, 45, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, ChunkDestroyerMod.CHUNK_DESTROYER_BLOCK.get());
    }
}
