package com.kaitu.chunkdestroyer.menu;

import com.kaitu.chunkdestroyer.ChunkDestroyerMod;
import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ChunkDestroyerMenu extends AbstractContainerMenu {
    private final ChunkDestroyerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    // クライアント側コンストラクタ (NetworkHooks.openScreenから呼ばれる)
    public ChunkDestroyerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    // サーバー側コンストラクタ
    public ChunkDestroyerMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ChunkDestroyerMod.CHUNK_DESTROYER_MENU.get(), containerId);
        checkContainerSize(playerInventory, 36);
        this.blockEntity = (ChunkDestroyerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.data = data;

        // 内部バッファスロット (3行 x 9列 = 27スロット)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), col + row * 9, 8 + col * 18, 116 + row * 18));
            }
        }

        // プレイヤーインベントリ (3行 x 9列)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 178 + row * 18));
            }
        }

        // プレイヤーホットバー (9スロット)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 236));
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

    public int getCurrentY() {
        return this.data.get(3);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        // 0..26: BlockEntity slots
        // 27..53: Player main inventory
        // 54..62: Player hotbar
        if (index < 27) {
            // BlockEntity -> Player
            if (!this.moveItemStackTo(sourceStack, 27, 63, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player -> BlockEntity
            if (!this.moveItemStackTo(sourceStack, 0, 27, false)) {
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
