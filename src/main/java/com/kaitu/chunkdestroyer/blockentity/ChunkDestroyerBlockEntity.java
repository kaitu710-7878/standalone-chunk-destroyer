package com.kaitu.chunkdestroyer.blockentity;

import com.kaitu.chunkdestroyer.ChunkDestroyerMod;
import com.kaitu.chunkdestroyer.block.ChunkDestroyerBlock;
import com.kaitu.chunkdestroyer.config.ModConfig;
import com.kaitu.chunkdestroyer.menu.ChunkDestroyerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChunkDestroyerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INVENTORY_SIZE = 27;

    // 内部アイテムストレージ
    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(() -> itemHandler);

    // 仮置きストレージ (インベントリが埋まっている場合の無限仮置きバッファ)
    // ※仮置きが存在する場合、新規に採掘を開始することはできない（すでに開始されている採掘には影響しない）
    private final List<ItemStack> pendingBuffer = new ArrayList<>();

    // チャンク指定 (設置チャンクを基準とした各方位への拡張チャンク数: 0 = 設置チャンクのみ)
    private int chunkNorth = 0;
    private int chunkSouth = 0;
    private int chunkWest = 0;
    private int chunkEast = 0;

    // 実際の採掘境界 (ブロック座標)
    private int minX;
    private int maxX;
    private int minY = -64;
    private int maxY = 64;
    private int minZ;
    private int maxZ;

    // 現在の採掘水平カーソル (垂直柱単位で掘るためX, Zのみ管理)
    private int currentX;
    private int currentZ;
    private boolean finished = false;

    // 設定・モード
    private boolean working = false;
    private boolean silkTouch = true;
    private boolean mineLiquids = true;
    private boolean voidStone = false;
    private boolean voidDirt = false;
    private boolean areaInitialized = false;

    // GUI同期データ (12スロット)
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> working ? 1 : 0;
                case 1 -> silkTouch ? 1 : 0;
                case 2 -> mineLiquids ? 1 : 0;
                case 3 -> voidStone ? 1 : 0;
                case 4 -> voidDirt ? 1 : 0;
                case 5 -> chunkNorth;
                case 6 -> chunkSouth;
                case 7 -> chunkWest;
                case 8 -> chunkEast;
                case 9 -> finished ? 100 : calculateProgress();
                case 10 -> hasPendingItems() ? 1 : 0;
                case 11 -> getPendingTotalCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> working = value != 0;
                case 1 -> silkTouch = value != 0;
                case 2 -> mineLiquids = value != 0;
                case 3 -> voidStone = value != 0;
                case 4 -> voidDirt = value != 0;
                case 5 -> chunkNorth = value;
                case 6 -> chunkSouth = value;
                case 7 -> chunkWest = value;
                case 8 -> chunkEast = value;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public ChunkDestroyerBlockEntity(BlockPos pos, BlockState state) {
        super(ChunkDestroyerMod.CHUNK_DESTROYER_BE.get(), pos, state);
    }

    private void ensureAreaInitialized() {
        if (!areaInitialized && level != null) {
            calculateBlockBounds();
            resetMiningCursor();
            areaInitialized = true;
            setChanged();
        }
    }

    private void calculateBlockBounds() {
        if (level == null) return;
        ChunkPos center = new ChunkPos(worldPosition);
        int minChunkX = center.x - Math.max(0, chunkWest);
        int maxChunkX = center.x + Math.max(0, chunkEast);
        int minChunkZ = center.z - Math.max(0, chunkNorth);
        int maxChunkZ = center.z + Math.max(0, chunkSouth);

        this.minX = minChunkX * 16;
        this.maxX = maxChunkX * 16 + 15;
        this.minZ = minChunkZ * 16;
        this.maxZ = maxChunkZ * 16 + 15;
        this.minY = level.getMinBuildHeight();
        this.maxY = Math.max(minY, worldPosition.getY() - 1);
    }

    public void setChunkRange(int north, int south, int west, int east) {
        int maxChunkLimit = ModConfig.COMMON.maxRadiusBlocks.get() / 16;
        this.chunkNorth = Math.max(0, Math.min(north, maxChunkLimit));
        this.chunkSouth = Math.max(0, Math.min(south, maxChunkLimit));
        this.chunkWest = Math.max(0, Math.min(west, maxChunkLimit));
        this.chunkEast = Math.max(0, Math.min(east, maxChunkLimit));

        calculateBlockBounds();
        resetMiningCursor();
        this.areaInitialized = true;
        setChanged();
        syncToClient();
    }

    public void resetMiningCursor() {
        calculateBlockBounds();
        this.currentX = this.minX;
        this.currentZ = this.minZ;
        this.finished = false;
        setChanged();
        syncToClient();
    }

    public boolean hasPendingItems() {
        return !pendingBuffer.isEmpty();
    }

    public int getPendingTotalCount() {
        int count = 0;
        for (ItemStack stack : pendingBuffer) {
            count += stack.getCount();
        }
        return count;
    }

    public boolean toggleWorking() {
        if (!this.working) {
            // 新規に採掘を開始しようとしている場合: 仮置きアイテムが存在すれば開始できない！
            if (hasPendingItems()) {
                syncToClient();
                return false;
            }
            this.working = true;
            this.finished = false;
        } else {
            // すでに稼働している場合はいつでも停止可能
            this.working = false;
        }

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(ChunkDestroyerBlock.WORKING) && state.getValue(ChunkDestroyerBlock.WORKING) != this.working) {
                level.setBlock(worldPosition, state.setValue(ChunkDestroyerBlock.WORKING, this.working), 3);
            }
        }
        setChanged();
        syncToClient();
        return true;
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

    public void toggleVoidStone() {
        this.voidStone = !this.voidStone;
        setChanged();
        syncToClient();
    }

    public void toggleVoidDirt() {
        this.voidDirt = !this.voidDirt;
        setChanged();
        syncToClient();
    }

    private int calculateProgress() {
        long totalColumns = (long) (maxX - minX + 1) * (maxZ - minZ + 1);
        if (totalColumns <= 0) return 0;
        long processedColumns = (long) (currentZ - minZ) * (maxX - minX + 1) + (currentX - minX);
        return (int) Math.min(100, Math.max(0, (processedColumns * 100) / totalColumns));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChunkDestroyerBlockEntity be) {
        be.tickServer((ServerLevel) level, pos, state);
    }

    /**
     * 本家チャンクデストロイヤー仕様: 垂直柱 (X, Z の全Yブロック) を一括掘削
     * パフォーマンス改善:
     * - アイテム化 (popResource / ItemEntity) を完全撤廃
     * - インベントリ満杯時は「仮置きストレージ」へ保持
     * - ストレージに空きができたら自動で仮置きから優先搬出
     */
    private void tickServer(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        ensureAreaInitialized();

        // 1. 仮置きストレージ内のアイテムを隣接チェストおよび内部バッファへ優先排出
        flushPendingBuffer(serverLevel, pos);

        // 2. 内部バッファのアイテムを隣接チェストへ排出
        pushInternalBufferToAdjacentStorage(serverLevel, pos);

        // 停止中または採掘完了時
        if (!working || finished) {
            if (state.getValue(ChunkDestroyerBlock.WORKING)) {
                serverLevel.setBlock(pos, state.setValue(ChunkDestroyerBlock.WORKING, false), 3);
            }
            return;
        }

        if (!state.getValue(ChunkDestroyerBlock.WORKING)) {
            serverLevel.setBlock(pos, state.setValue(ChunkDestroyerBlock.WORKING, true), 3);
        }

        // デフォルトでアップグレード最大搭載速度
        int columnsPerTick = Math.max(1, ModConfig.COMMON.blocksPerTick.get() / 4);
        boolean allowBedrock = ModConfig.COMMON.mineBedrock.get();
        boolean removeFluidsConfig = ModConfig.COMMON.removeFluids.get() && this.mineLiquids;

        // ドロップ計算用の擬似ツール
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        if (silkTouch) {
            tool.enchant(Enchantments.SILK_TOUCH, 1);
        } else {
            tool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        }
        tool.enchant(Enchantments.BLOCK_EFFICIENCY, 5);

        List<ItemStack> collectedDrops = new ArrayList<>();
        int columnsProcessed = 0;
        BlockPos.MutableBlockPos columnPos = new BlockPos.MutableBlockPos();

        while (columnsProcessed < columnsPerTick && !finished) {
            // (currentX, currentZ) の垂直柱を一括採掘
            for (int y = maxY; y >= minY; y--) {
                columnPos.set(currentX, y, currentZ);

                if (columnPos.equals(pos) || !serverLevel.isLoaded(columnPos)) {
                    continue;
                }

                BlockState targetState = serverLevel.getBlockState(columnPos);
                if (targetState.isAir()) continue;

                boolean isFluid = !targetState.getFluidState().isEmpty();
                if (isFluid) {
                    if (removeFluidsConfig) {
                        serverLevel.setBlock(columnPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                } else {
                    boolean isBedrock = targetState.is(Blocks.BEDROCK);
                    if (!isBedrock || allowBedrock) {
                        BlockEntity targetBe = serverLevel.getBlockEntity(columnPos);
                        LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(columnPos))
                                .withParameter(LootContextParams.TOOL, tool);
                        if (targetBe != null) {
                            lootBuilder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, targetBe);
                        }

                        List<ItemStack> drops = targetState.getDrops(lootBuilder);
                        for (ItemStack drop : drops) {
                            if (voidStone && ItemFilterHelper.isStone(drop)) continue;
                            if (voidDirt && ItemFilterHelper.isDirt(drop)) continue;
                            mergeIntoList(collectedDrops, drop);
                        }

                        // ブロックを空気に置換 (フラグ2: UPDATE_CLIENTS のみで近傍更新連鎖を防止)
                        serverLevel.setBlock(columnPos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }

            columnsProcessed++;

            currentX++;
            if (currentX > maxX) {
                currentX = minX;
                currentZ++;
                if (currentZ > maxZ) {
                    finished = true;
                    working = false;
                    break;
                }
            }
        }

        // 回収したドロップをストレージに格納 (溢れた分は仮置きストレージへ)
        if (!collectedDrops.isEmpty()) {
            distributeDrops(serverLevel, pos, collectedDrops);
        }

        if (columnsProcessed > 0) {
            setChanged();
        }
    }

    private void mergeIntoList(List<ItemStack> list, ItemStack stack) {
        if (stack.isEmpty()) return;
        for (ItemStack existing : list) {
            if (ItemHandlerHelper.canItemStacksStack(existing, stack)) {
                existing.grow(stack.getCount());
                return;
            }
        }
        list.add(stack.copy());
    }

    private void distributeDrops(ServerLevel level, BlockPos pos, List<ItemStack> drops) {
        List<IItemHandler> adjacentHandlers = getAdjacentItemHandlers(level, pos);

        for (ItemStack stack : drops) {
            if (stack.isEmpty()) continue;

            if (voidStone && ItemFilterHelper.isStone(stack)) continue;
            if (voidDirt && ItemFilterHelper.isDirt(stack)) continue;

            ItemStack remaining = stack.copy();

            // 1. 隣接ストレージへの格納
            for (IItemHandler handler : adjacentHandlers) {
                remaining = ItemHandlerHelper.insertItemStacked(handler, remaining, false);
                if (remaining.isEmpty()) {
                    break;
                }
            }

            // 2. 内部バッファへの格納
            if (!remaining.isEmpty()) {
                remaining = ItemHandlerHelper.insertItemStacked(this.itemHandler, remaining, false);
            }

            // 3. パフォーマンス改善: アイテム化せず、仮置きストレージへ格納！
            if (!remaining.isEmpty()) {
                addToPendingBuffer(remaining);
            }
        }
    }

    private void addToPendingBuffer(ItemStack stack) {
        if (stack.isEmpty()) return;
        for (ItemStack existing : pendingBuffer) {
            if (ItemHandlerHelper.canItemStacksStack(existing, stack)) {
                existing.grow(stack.getCount());
                setChanged();
                return;
            }
        }
        pendingBuffer.add(stack.copy());
        setChanged();
    }

    private void flushPendingBuffer(ServerLevel level, BlockPos pos) {
        if (pendingBuffer.isEmpty()) return;

        List<IItemHandler> adjacentHandlers = getAdjacentItemHandlers(level, pos);
        boolean changed = false;

        for (int i = pendingBuffer.size() - 1; i >= 0; i--) {
            ItemStack stack = pendingBuffer.get(i);
            if (stack.isEmpty()) {
                pendingBuffer.remove(i);
                changed = true;
                continue;
            }

            if (voidStone && ItemFilterHelper.isStone(stack)) {
                pendingBuffer.remove(i);
                changed = true;
                continue;
            }
            if (voidDirt && ItemFilterHelper.isDirt(stack)) {
                pendingBuffer.remove(i);
                changed = true;
                continue;
            }

            ItemStack remaining = stack;

            // 1. 隣接ストレージへ搬出
            for (IItemHandler handler : adjacentHandlers) {
                remaining = ItemHandlerHelper.insertItemStacked(handler, remaining, false);
                if (remaining.isEmpty()) break;
            }

            // 2. 内部バッファへ搬出
            if (!remaining.isEmpty()) {
                remaining = ItemHandlerHelper.insertItemStacked(this.itemHandler, remaining, false);
            }

            if (remaining.isEmpty()) {
                pendingBuffer.remove(i);
                changed = true;
            } else if (remaining.getCount() != stack.getCount()) {
                pendingBuffer.set(i, remaining);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            syncToClient();
        }
    }

    private void pushInternalBufferToAdjacentStorage(ServerLevel level, BlockPos pos) {
        List<IItemHandler> adjacentHandlers = getAdjacentItemHandlers(level, pos);
        if (adjacentHandlers.isEmpty()) return;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) continue;

            if (voidStone && ItemFilterHelper.isStone(stackInSlot)) {
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                setChanged();
                continue;
            }
            if (voidDirt && ItemFilterHelper.isDirt(stackInSlot)) {
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                setChanged();
                continue;
            }

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

        // 仮置きストレージのNBT保存 (無限スタック数対応)
        ListTag pendingTag = new ListTag();
        for (ItemStack stack : pendingBuffer) {
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (key != null) {
                    itemTag.putString("id", key.toString());
                    itemTag.putInt("count", stack.getCount());
                    if (stack.hasTag()) {
                        itemTag.put("tag", stack.getTag().copy());
                    }
                    pendingTag.add(itemTag);
                }
            }
        }
        tag.put("PendingBuffer", pendingTag);

        tag.putInt("ChunkNorth", chunkNorth);
        tag.putInt("ChunkSouth", chunkSouth);
        tag.putInt("ChunkWest", chunkWest);
        tag.putInt("ChunkEast", chunkEast);

        tag.putInt("CurrentX", currentX);
        tag.putInt("CurrentZ", currentZ);
        tag.putBoolean("Finished", finished);

        tag.putBoolean("Working", working);
        tag.putBoolean("SilkTouch", silkTouch);
        tag.putBoolean("MineLiquids", mineLiquids);
        tag.putBoolean("VoidStone", voidStone);
        tag.putBoolean("VoidDirt", voidDirt);
        tag.putBoolean("AreaInitialized", areaInitialized);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }

        // 仮置きストレージのNBT読み込み
        pendingBuffer.clear();
        if (tag.contains("PendingBuffer", Tag.TAG_LIST)) {
            ListTag pendingTag = tag.getList("PendingBuffer", Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingTag.size(); i++) {
                CompoundTag itemTag = pendingTag.getCompound(i);
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemTag.getString("id")));
                if (item != null && item != Items.AIR) {
                    int count = itemTag.getInt("count");
                    ItemStack stack = new ItemStack(item, Math.min(count, 64));
                    stack.setCount(count); // 大容量カウント
                    if (itemTag.contains("tag", Tag.TAG_COMPOUND)) {
                        stack.setTag(itemTag.getCompound("tag"));
                    }
                    pendingBuffer.add(stack);
                }
            }
        }

        this.chunkNorth = tag.getInt("ChunkNorth");
        this.chunkSouth = tag.getInt("ChunkSouth");
        this.chunkWest = tag.getInt("ChunkWest");
        this.chunkEast = tag.getInt("ChunkEast");

        this.currentX = tag.getInt("CurrentX");
        this.currentZ = tag.getInt("CurrentZ");
        this.finished = tag.getBoolean("Finished");

        this.working = tag.contains("Working") && tag.getBoolean("Working");
        this.silkTouch = !tag.contains("SilkTouch") || tag.getBoolean("SilkTouch");
        this.mineLiquids = !tag.contains("MineLiquids") || tag.getBoolean("MineLiquids");
        this.voidStone = tag.getBoolean("VoidStone");
        this.voidDirt = tag.getBoolean("VoidDirt");
        this.areaInitialized = tag.getBoolean("AreaInitialized");

        calculateBlockBounds();
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
    public int getChunkNorth() { return chunkNorth; }
    public int getChunkSouth() { return chunkSouth; }
    public int getChunkWest() { return chunkWest; }
    public int getChunkEast() { return chunkEast; }

    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }
    public int getMinZ() { return minZ; }
    public int getMaxZ() { return maxZ; }

    public boolean isWorking() { return working; }
    public boolean isSilkTouch() { return silkTouch; }
    public boolean isMineLiquids() { return mineLiquids; }
    public boolean isVoidStone() { return voidStone; }
    public boolean isVoidDirt() { return voidDirt; }

    public ItemStackHandler getItemHandler() { return itemHandler; }
    public List<ItemStack> getPendingBuffer() { return pendingBuffer; }
}
