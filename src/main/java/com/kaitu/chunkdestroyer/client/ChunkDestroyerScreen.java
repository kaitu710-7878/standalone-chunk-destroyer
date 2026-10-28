package com.kaitu.chunkdestroyer.client;

import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import com.kaitu.chunkdestroyer.menu.ChunkDestroyerMenu;
import com.kaitu.chunkdestroyer.network.ModNetwork;
import com.kaitu.chunkdestroyer.network.PacketToggleState;
import com.kaitu.chunkdestroyer.network.PacketUpdateArea;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChunkDestroyerScreen extends AbstractContainerScreen<ChunkDestroyerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("chunkdestroyer", "textures/gui/chunk_destroyer_gui.png");

    private EditBox northBox;
    private EditBox southBox;
    private EditBox westBox;
    private EditBox eastBox;

    private Button toggleWorkingBtn;
    private Button toggleSilkBtn;
    private Button toggleLiquidBtn;
    private Button toggleVoidStoneBtn;
    private Button toggleVoidDirtBtn;

    public ChunkDestroyerScreen(ChunkDestroyerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 210;
        this.inventoryLabelY = 117;
    }

    @Override
    protected void init() {
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        ChunkDestroyerBlockEntity be = menu.getBlockEntity();

        // --- 中央エリア: 十字方位チャンク指定 (N, S, W, E + キーボード直接数値入力) ---
        // 北 (North): 上部 (Y=17)
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(northBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_north")))
                .bounds(x + 101, y + 17, 12, 14).build());
        northBox = createNumericBox(x + 115, y + 17, 22, 14, be.getChunkNorth());
        this.addRenderableWidget(northBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(northBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_north")))
                .bounds(x + 139, y + 17, 12, 14).build());

        // 西 (West): 中央左 (Y=43)
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(westBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_west")))
                .bounds(x + 65, y + 43, 12, 14).build());
        westBox = createNumericBox(x + 79, y + 43, 22, 14, be.getChunkWest());
        this.addRenderableWidget(westBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(westBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_west")))
                .bounds(x + 103, y + 43, 12, 14).build());

        // 中央: 範囲適用ボタン (Y=43)
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.apply"), b -> sendAreaUpdate())
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_apply")))
                .bounds(x + 117, y + 43, 18, 14).build());

        // 東 (East): 中央右 (Y=43)
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(eastBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_east")))
                .bounds(x + 137, y + 43, 12, 14).build());
        eastBox = createNumericBox(x + 151, y + 43, 22, 14, be.getChunkEast());
        this.addRenderableWidget(eastBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(eastBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_east")))
                .bounds(x + 175, y + 43, 12, 14).build());

        // 南 (South): 下部 (Y=69)
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(southBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_south")))
                .bounds(x + 101, y + 69, 12, 14).build());
        southBox = createNumericBox(x + 115, y + 69, 22, 14, be.getChunkSouth());
        this.addRenderableWidget(southBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(southBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_south")))
                .bounds(x + 139, y + 69, 12, 14).build());

        // --- 右側エリア: 縦並び機能コントロールボタン群 (X=194, 幅56) ---
        // 1. 稼働切替 (Y=6)
        toggleWorkingBtn = Button.builder(getWorkingButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_WORKING));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_working")))
          .bounds(x + 194, y + 6, 56, 14).build();
        this.addRenderableWidget(toggleWorkingBtn);

        // 2. シルク/幸運 (Y=22)
        toggleSilkBtn = Button.builder(getSilkButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_SILK));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_silk")))
          .bounds(x + 194, y + 22, 56, 14).build();
        this.addRenderableWidget(toggleSilkBtn);

        // 3. 液体 (Y=38)
        toggleLiquidBtn = Button.builder(getLiquidButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_LIQUIDS));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_liquid")))
          .bounds(x + 194, y + 38, 56, 14).build();
        this.addRenderableWidget(toggleLiquidBtn);

        // 4. 石削除 (Y=54)
        toggleVoidStoneBtn = Button.builder(getVoidStoneButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_VOID_STONE));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_void_stone")))
          .bounds(x + 194, y + 54, 56, 14).build();
        this.addRenderableWidget(toggleVoidStoneBtn);

        // 5. 土削除 (Y=70)
        toggleVoidDirtBtn = Button.builder(getVoidDirtButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_VOID_DIRT));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_void_dirt")))
          .bounds(x + 194, y + 70, 56, 14).build();
        this.addRenderableWidget(toggleVoidDirtBtn);

        // 6. 採掘リセット (Y=86)
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.reset"), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_RESET_POS));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_reset")))
          .bounds(x + 194, y + 86, 56, 14).build());
    }

    private int getStep() {
        return Screen.hasShiftDown() ? 16 : 1;
    }

    private EditBox createNumericBox(int x, int y, int width, int height, int initialValue) {
        EditBox box = new EditBox(this.font, x, y, width, height, Component.literal(""));
        box.setValue(String.valueOf(initialValue));
        box.setFilter(text -> text.isEmpty() || text.matches("^\\d+$"));
        return box;
    }

    private void adjustBox(EditBox box, int delta) {
        try {
            int current = Integer.parseInt(box.getValue().isEmpty() ? "0" : box.getValue());
            int newVal = Math.max(0, current + delta);
            box.setValue(String.valueOf(newVal));
            sendAreaUpdate();
        } catch (NumberFormatException ignored) {}
    }

    private void sendAreaUpdate() {
        try {
            int north = Integer.parseInt(northBox.getValue().isEmpty() ? "0" : northBox.getValue());
            int south = Integer.parseInt(southBox.getValue().isEmpty() ? "0" : southBox.getValue());
            int west = Integer.parseInt(westBox.getValue().isEmpty() ? "0" : westBox.getValue());
            int east = Integer.parseInt(eastBox.getValue().isEmpty() ? "0" : eastBox.getValue());

            ChunkDestroyerBlockEntity be = menu.getBlockEntity();
            ModNetwork.CHANNEL.sendToServer(new PacketUpdateArea(be.getBlockPos(), north, south, west, east));
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter
            sendAreaUpdate();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Component getWorkingButtonText() {
        return menu.isWorking()
                ? Component.translatable("gui.chunkdestroyer.stop")
                : Component.translatable("gui.chunkdestroyer.start");
    }

    private Component getSilkButtonText() {
        return menu.isSilkTouch()
                ? Component.translatable("gui.chunkdestroyer.silk")
                : Component.translatable("gui.chunkdestroyer.fortune");
    }

    private Component getLiquidButtonText() {
        return menu.isMineLiquids()
                ? Component.translatable("gui.chunkdestroyer.liquid_remove")
                : Component.translatable("gui.chunkdestroyer.liquid_keep");
    }

    private Component getVoidStoneButtonText() {
        return menu.isVoidStone()
                ? Component.translatable("gui.chunkdestroyer.void_stone_on")
                : Component.translatable("gui.chunkdestroyer.void_stone_off");
    }

    private Component getVoidDirtButtonText() {
        return menu.isVoidDirt()
                ? Component.translatable("gui.chunkdestroyer.void_dirt_on")
                : Component.translatable("gui.chunkdestroyer.void_dirt_off");
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // --- 左側エリア: タイトル・稼働状態・進捗・仮置き情報 ---
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);

        Component statusComponent = menu.isWorking()
                ? Component.translatable("gui.chunkdestroyer.status_running")
                : Component.translatable("gui.chunkdestroyer.status_stopped");
        int statusColor = menu.isWorking() ? 0x228B22 : 0xB22222;
        guiGraphics.drawString(this.font, statusComponent, 8, 22, statusColor, false);

        Component progressComponent = Component.translatable("gui.chunkdestroyer.progress", menu.getProgress());
        guiGraphics.drawString(this.font, progressComponent, 8, 38, 0x555555, false);

        Component pendingComponent = Component.translatable("gui.chunkdestroyer.pending", menu.getPendingTotalCount());
        int pendingColor = menu.hasPendingItems() ? 0xC0392B : 0x777777;
        guiGraphics.drawString(this.font, pendingComponent, 8, 54, pendingColor, false);

        // --- 中央エリア: 方位ラベル (N, S, W, E) ---
        guiGraphics.drawString(this.font, "N", 123, 6, 0x404040, false);
        guiGraphics.drawString(this.font, "W", 86, 32, 0x404040, false);
        guiGraphics.drawString(this.font, "E", 158, 32, 0x404040, false);
        guiGraphics.drawString(this.font, "S", 123, 86, 0x404040, false);

        // --- インベントリラベル (X=47にセンタリング) ---
        guiGraphics.drawString(this.font, Component.translatable("container.chunkdestroyer.buffer"), 47, 95, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 47, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (toggleWorkingBtn != null) {
            toggleWorkingBtn.setMessage(getWorkingButtonText());
            if (!menu.isWorking() && menu.hasPendingItems()) {
                toggleWorkingBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_working_blocked")));
            } else {
                toggleWorkingBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_working")));
            }
        }
        if (toggleSilkBtn != null) toggleSilkBtn.setMessage(getSilkButtonText());
        if (toggleLiquidBtn != null) toggleLiquidBtn.setMessage(getLiquidButtonText());
        if (toggleVoidStoneBtn != null) toggleVoidStoneBtn.setMessage(getVoidStoneButtonText());
        if (toggleVoidDirtBtn != null) toggleVoidDirtBtn.setMessage(getVoidDirtButtonText());

        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
