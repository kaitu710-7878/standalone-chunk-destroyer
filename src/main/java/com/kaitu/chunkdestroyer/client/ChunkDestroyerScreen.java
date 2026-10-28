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
        this.imageWidth = 176;
        this.imageHeight = 210;
        this.inventoryLabelY = 117;
    }

    @Override
    protected void init() {
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        ChunkDestroyerBlockEntity be = menu.getBlockEntity();

        // --- 十字方位チャンク指定 (本家AdvQuarryスタイル + キーボード直接数値入力) ---
        // 北 (North): 上
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(northBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_north")))
                .bounds(x + 56, y + 17, 14, 14).build());
        northBox = createNumericBox(x + 72, y + 17, 32, 14, be.getChunkNorth());
        this.addRenderableWidget(northBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(northBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_north")))
                .bounds(x + 106, y + 17, 14, 14).build());

        // 南 (South): 下
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(southBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_south")))
                .bounds(x + 56, y + 55, 14, 14).build());
        southBox = createNumericBox(x + 72, y + 55, 32, 14, be.getChunkSouth());
        this.addRenderableWidget(southBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(southBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_south")))
                .bounds(x + 106, y + 55, 14, 14).build());

        // 西 (West): 左
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(westBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_west")))
                .bounds(x + 6, y + 36, 14, 14).build());
        westBox = createNumericBox(x + 22, y + 36, 30, 14, be.getChunkWest());
        this.addRenderableWidget(westBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(westBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_west")))
                .bounds(x + 54, y + 36, 14, 14).build());

        // 東 (East): 右
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> adjustBox(eastBox, -getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_minus_east")))
                .bounds(x + 108, y + 36, 14, 14).build());
        eastBox = createNumericBox(x + 124, y + 36, 30, 14, be.getChunkEast());
        this.addRenderableWidget(eastBox);
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> adjustBox(eastBox, getStep()))
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_plus_east")))
                .bounds(x + 156, y + 36, 14, 14).build());

        // 中央: 範囲適用ボタン
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.apply"), b -> sendAreaUpdate())
                .tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_apply")))
                .bounds(x + 72, y + 36, 32, 14).build());

        // --- 機能コントロールボタン群 ---
        // 1段目: 稼働トグル、シルク/幸運、液体トグル
        toggleWorkingBtn = Button.builder(getWorkingButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_WORKING));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_working")))
          .bounds(x + 6, y + 73, 52, 14).build();
        this.addRenderableWidget(toggleWorkingBtn);

        toggleSilkBtn = Button.builder(getSilkButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_SILK));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_silk")))
          .bounds(x + 61, y + 73, 54, 14).build();
        this.addRenderableWidget(toggleSilkBtn);

        toggleLiquidBtn = Button.builder(getLiquidButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_LIQUIDS));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_liquid")))
          .bounds(x + 118, y + 73, 52, 14).build();
        this.addRenderableWidget(toggleLiquidBtn);

        // 2段目: 石削除トグル、土削除トグル、採掘リセット
        toggleVoidStoneBtn = Button.builder(getVoidStoneButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_VOID_STONE));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_void_stone")))
          .bounds(x + 6, y + 89, 52, 14).build();
        this.addRenderableWidget(toggleVoidStoneBtn);

        toggleVoidDirtBtn = Button.builder(getVoidDirtButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_VOID_DIRT));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_void_dirt")))
          .bounds(x + 61, y + 89, 54, 14).build();
        this.addRenderableWidget(toggleVoidDirtBtn);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.reset"), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_RESET_POS));
        }).tooltip(Tooltip.create(Component.translatable("tooltip.chunkdestroyer.btn_reset")))
          .bounds(x + 118, y + 89, 52, 14).build());
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
        // タイトル
        guiGraphics.drawString(this.font, this.title, 6, 6, 0x404040, false);

        // 状態インジケータ (右上に表示)
        Component statusComponent = menu.isWorking()
                ? Component.translatable("gui.chunkdestroyer.status_running")
                : Component.translatable("gui.chunkdestroyer.status_stopped");
        int statusColor = menu.isWorking() ? 0x228B22 : 0xB22222;
        guiGraphics.drawString(this.font, statusComponent, 130, 6, statusColor, false);

        // 仮置きストレージのアイテム表示
        if (menu.hasPendingItems()) {
            Component pendingComponent = Component.translatable("gui.chunkdestroyer.pending", menu.getPendingTotalCount());
            guiGraphics.drawString(this.font, pendingComponent, 6, 20, 0xC0392B, false);
        }

        // 方位ラベル (N, S, W, E)
        guiGraphics.drawString(this.font, "N", 84, 8, 0x404040, false);
        guiGraphics.drawString(this.font, "S", 84, 48, 0x404040, false);
        guiGraphics.drawString(this.font, "W", 34, 26, 0x404040, false);
        guiGraphics.drawString(this.font, "E", 136, 26, 0x404040, false);

        // インベントリラベル
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
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
