package com.kaitu.chunkdestroyer.blockentity;

import com.kaitu.chunkdestroyer.ChunkDestroyerMod;
import com.kaitu.chunkdestroyer.block.ChunkDestroyerBlock;
import com.kaitu.chunkdestroyer.config.ModConfig;
import com.kaitu.chunkdestroyer.menu.ChunkDestroyerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChunkDestroyerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INVENTORY_SIZE = 27;

    // 内部アイテムストレージ (27スロット)
    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(() -> itemHandler);

    // 範囲設定 (デフォルト: 設置チャンクの境界 16x16, Yはワールド底〜現在Y-1)
    private int minX;
    private int maxX;
    private int minY = -64;
    private int maxY = 64;
    private int minZ;
    private int maxZ;

    // 現在の採掘カーソル
    private int currentX;
    private int currentY;
    private int currentZ;

    // 稼働状態・設定フラグ
    private boolean working = false;
    private boolean silkTouch = true;
    private boolean mineLiquids = true;
    private boolean areaInitialized = false;

    // コンテナデータ (GUIとの同期用: 0:working, 1:silkTouch, 2:mineLiquids, 3:progressPercent, etc)
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> working ? 1 : 0;
                case 1 -> silkTouch ? 1 : 0;
                case 2 -> mineLiquids ? 1 : 0;
                case 3 -> currentY;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> working = value != 0;
                case 1 -> silkTouch = value != 0;
                case 2 -> mineLiquids = value != 0;
                case 3 -> currentY = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public ChunkDestroyerBlockEntity(BlockPos pos, BlockState state) {
        super(ChunkDestroyerMod.CHUNK_DESTROYER_BE.get(), pos, state);
    }

    private void ensureAreaInitialized() {
        if (!areaInitialized && level != null) {
            ChunkPos chunkPos = new ChunkPos(worldPosition);
            this.minX = chunkPos.getMinBlockX();
            this.maxX = chunkPos.getMaxBlockX();
            this.minZ = chunkPos.getMinBlockZ();
            this.maxZ = chunkPos.getMaxBlockZ();
            this.minY = level.getMinBuildHeight();
            this.maxY = Math.max(minY, worldPosition.getY() - 1);

            resetMiningCursor();
            areaInitialized = true;
            setChanged();
        }
    }

    public void resetMiningCursor() {
        this.currentY = this.maxY;
        this.currentX = this.minX;
        this.currentZ = this.minZ;
        setChanged();
        syncToClient();
    }

    public void setMiningArea(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int maxRadius = ModConfig.COMMON.maxRadiusBlocks.get();
        this.minX = Math.max(worldPosition.getX() - maxRadius, Math.min(minX, maxX));
        this.maxX = Math.min(worldPosition.getX() + maxRadius, Math.max(minX, maxX));

        int minWorldY = level != null ? level.getMinBuildHeight() : -64;
        int maxWorldY = level != null ? level.getMaxBuildHeight() : 320;
        this.minY = Math.max(minWorldY, Math.min(minY, maxY));
        this.maxY = Math.min(maxWorldY, Math.max(minY, maxY));

        this.minZ = Math.max(worldPosition.getZ() - maxRadius, Math.min(minZ, maxZ));
        this.maxZ = Math.min(worldPosition.getZ() + maxRadius, Math.max(minZ, maxZ));

        this.areaInitialized = true;
        resetMiningCursor();
    }

    public void toggleWorking() {
        this.working = !this.working;
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(ChunkDestroyerBlock.WORKING) && state.getValue(ChunkDestroyerBlock.WORKING) != this.working) {
                level.setBlock(worldPosition, state.setValue(ChunkDestroyerBlock.WORKING, this.working), 3);
            }
        }
        setChanged();
        syncToClient();
    }

    public void toggleSilkTouch() {
        this.silkTouch = !this.silkTouch;
        setChanged();
        syncToClient();
    }

    public void toggleLiquids() {
        this.mineLiquids = !this.mineLiquids;
        setChanged();
        syncToClient();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChunkDestroyerBlockEntity be) {
        be.tickServer((ServerLevel) level, pos, state);
    }

    private void tickServer(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        ensureAreaInitialized();

        // 内部バッファのアイテムを隣接チェストへ排出
        pushInternalBufferToAdjacentStorage(serverLevel, pos);

        if (!working) {
            if (state.getValue(ChunkDestroyerBlock.WORKING)) {
                serverLevel.setBlock(pos, state.setValue(ChunkDestroyerBlock.WORKING, false), 3);
            }
            return;
        }

        // 採掘完了チェック
        if (currentY < minY) {
            // 範囲内の全ブロック採掘完了
            if (state.getValue(ChunkDestroyerBlock.WORKING)) {
                serverLevel.setBlock(pos, state.setValue(ChunkDestroyerBlock.WORKING, false), 3);
            }
            return;
        }

        if (!state.getValue(ChunkDestroyerBlock.WORKING)) {
            serverLevel.setBlock(pos, state.setValue(ChunkDestroyerBlock.WORKING, true), 3);
        }

        // デフォルトでアップグレード最大搭載速度
        int blocksPerTick = ModConfig.COMMON.blocksPerTick.get();
        boolean allowBedrock = ModConfig.COMMON.mineBedrock.get();
        boolean removeFluidsConfig = ModConfig.COMMON.removeFluids.get() && this.mineLiquids;

        // ドロップ計算用の擬似ツール（シルクタッチ または 幸運III）
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        if (silkTouch) {
            tool.enchant(Enchantments.SILK_TOUCH, 1);
        } else {
            tool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        }
        tool.enchant(Enchantments.BLOCK_EFFICIENCY, 5);

        List<ItemStack> collectedDrops = new ArrayList<>();
        int blocksDestroyedThisTick = 0;
        int maxScanSteps = blocksPerTick * 8; // 空気などをスキップするための最大ステップ上限
        int scanSteps = 0;

        BlockPos.MutableBlockPos targetPos = new BlockPos.MutableBlockPos();

        while (blocksDestroyedThisTick < blocksPerTick && currentY >= minY && scanSteps < maxScanSteps) {
            scanSteps++;
            targetPos.set(currentX, currentY, currentZ);

            // 自身の位置は掘らない
            if (!targetPos.equals(pos) && serverLevel.isLoaded(targetPos)) {
                BlockState targetState = serverLevel.getBlockState(targetPos);

                if (!targetState.isAir()) {
                    boolean isFluid = !targetState.getFluidState().isEmpty();

                    if (isFluid) {
                        if (removeFluidsConfig) {
                            // 液体を一括消去（近傍更新なしでAIRに置換）
                            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 2);
                            blocksDestroyedThisTick++;
                        }
                    } else {
                        boolean isBedrock = targetState.is(Blocks.BEDROCK);
                        if (!isBedrock || allowBedrock) {
                            // パフォーマンス改善: ItemEntity をスポーンさせず、ドロップを直接回収
                            BlockEntity targetBe = serverLevel.getBlockEntity(targetPos);
                            LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetPos))
                                    .withParameter(LootContextParams.TOOL, tool);
                            if (targetBe != null) {
                                lootBuilder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, targetBe);
                            }

                            List<ItemStack> drops = targetState.getDrops(lootBuilder);
                            collectedDrops.addAll(drops);

                            // ブロックを空気にして消去 (フラグ2: UPDATE_CLIENTS のみで近傍更新の連鎖を防ぐ)
                            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 2);
                            blocksDestroyedThisTick++;
                        }
                    }
                }
            }

            // 次の座標へインクリメント
            advanceCursor();
        }

        // 回収したドロップを隣接ストレージ、または内部バッファに一括格納
        if (!collectedDrops.isEmpty()) {
            distributeDrops(serverLevel, pos, collectedDrops);
        }

        if (blocksDestroyedThisTick > 0) {
            setChanged();
        }
    }

    private void advanceCursor() {
        currentX++;
        if (currentX > maxX) {
            currentX = minX;
            currentZ++;
            if (currentZ > maxZ) {
                currentZ = minZ;
                currentY--;
            }
        }
    }

    private void distributeDrops(ServerLevel level, BlockPos pos, List<ItemStack> drops) {
        List<IItemHandler> adjacentHandlers = getAdjacentItemHandlers(level, pos);

        for (ItemStack stack : drops) {
            if (stack.isEmpty()) continue;

            ItemStack remaining = stack.copy();

            // 1. 隣接ストレージへの格納を試行
            for (IItemHandler handler : adjacentHandlers) {
                remaining = ItemHandlerHelper.insertItemStacked(handler, remaining, false);
                if (remaining.isEmpty()) {
                    break;
                }
            }

            // 2. 残った分は自身の内部バッファに格納
            if (!remaining.isEmpty()) {
                remaining = ItemHandlerHelper.insertItemStacked(this.itemHandler, remaining, false);
            }

            // 3. それでも溢れた場合は機械の直上に安全にドロップ
            if (!remaining.isEmpty()) {
                Block.popResource(level, pos.above(), remaining);
            }
        }
    }

    private void pushInternalBufferToAdjacentStorage(ServerLevel level, BlockPos pos) {
        List<IItemHandler> adjacentHandlers = getAdjacentItemHandlers(level, pos);
        if (adjacentHandlers.isEmpty()) return;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) continue;

            ItemStack toInsert = stackInSlot.copy();
            for (IItemHandler handler : adjacentHandlers) {
                toInsert = ItemHandlerHelper.insertItemStacked(handler, toInsert, false);
                if (toInsert.isEmpty()) break;
            }

            if (toInsert.getCount() != stackInSlot.getCount()) {
                itemHandler.setStackInSlot(i, toInsert);
                setChanged();
            }
        }
    }

    private List<IItemHandler> getAdjacentItemHandlers(ServerLevel level, BlockPos pos) {
        List<IItemHandler> handlers = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockEntity neighborBe = level.getBlockEntity(neighborPos);
            if (neighborBe != null && !(neighborBe instanceof ChunkDestroyerBlockEntity)) {
                LazyOptional<IItemHandler> cap = neighborBe.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
                cap.ifPresent(handlers::add);
            }
        }
        return handlers;
    }

    public void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("MinX", minX);
        tag.putInt("MaxX", maxX);
        tag.putInt("MinY", minY);
        tag.putInt("MaxY", maxY);
        tag.putInt("MinZ", minZ);
        tag.putInt("MaxZ", maxZ);

        tag.putInt("CurrentX", currentX);
        tag.putInt("CurrentY", currentY);
        tag.putInt("CurrentZ", currentZ);

        tag.putBoolean("Working", working);
        tag.putBoolean("SilkTouch", silkTouch);
        tag.putBoolean("MineLiquids", mineLiquids);
        tag.putBoolean("AreaInitialized", areaInitialized);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        this.minX = tag.getInt("MinX");
        this.maxX = tag.getInt("MaxX");
        this.minY = tag.getInt("MinY");
        this.maxY = tag.getInt("MaxY");
        this.minZ = tag.getInt("MinZ");
        this.maxZ = tag.getInt("MaxZ");

        this.currentX = tag.getInt("CurrentX");
        this.currentY = tag.getInt("CurrentY");
        this.currentZ = tag.getInt("CurrentZ");

        this.working = tag.contains("Working") && tag.getBoolean("Working");
        this.silkTouch = !tag.contains("SilkTouch") || tag.getBoolean("SilkTouch");
        this.mineLiquids = !tag.contains("MineLiquids") || tag.getBoolean("MineLiquids");
        this.areaInitialized = tag.getBoolean("AreaInitialized");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chunkdestroyer.chunk_destroyer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ensureAreaInitialized();
        return new ChunkDestroyerMenu(containerId, playerInventory, this, this.dataAccess);
    }

    // ゲッター群
    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public int getMinZ() { return minZ; }
    public int getMaxZ() { return maxZ; }

    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
    public int getCurrentZ() { return currentZ; }

    public boolean isWorking() { return working; }
    public boolean isSilkTouch() { return silkTouch; }
    public boolean isMineLiquids() { return mineLiquids; }

    public ItemStackHandler getItemHandler() { return itemHandler; }
}
