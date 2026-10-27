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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChunkDestroyerScreen extends AbstractContainerScreen<ChunkDestroyerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("chunkdestroyer", "textures/gui/chunk_destroyer_gui.png");

    private EditBox minXBox;
    private EditBox maxXBox;
    private EditBox minYBox;
    private EditBox maxYBox;
    private EditBox minZBox;
    private EditBox maxZBox;

    private EditBox lastFocusedBox = null;

    private Button toggleWorkingBtn;
    private Button toggleSilkBtn;
    private Button toggleLiquidBtn;

    public ChunkDestroyerScreen(ChunkDestroyerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 260;
        this.inventoryLabelY = 166;
    }

    @Override
    protected void init() {
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        ChunkDestroyerBlockEntity be = menu.getBlockEntity();

        // 範囲入力 EditBox (直接キーボードで数字入力可能)
        // X
        minXBox = createNumericBox(x + 36, y + 20, 42, 14, be.getMinX());
        maxXBox = createNumericBox(x + 82, y + 20, 42, 14, be.getMaxX());

        // Y
        minYBox = createNumericBox(x + 36, y + 36, 42, 14, be.getMinY());
        maxYBox = createNumericBox(x + 82, y + 36, 42, 14, be.getMaxY());

        // Z
        minZBox = createNumericBox(x + 36, y + 52, 42, 14, be.getMinZ());
        maxZBox = createNumericBox(x + 82, y + 52, 42, 14, be.getMaxZ());

        this.addRenderableWidget(minXBox);
        this.addRenderableWidget(maxXBox);
        this.addRenderableWidget(minYBox);
        this.addRenderableWidget(maxYBox);
        this.addRenderableWidget(minZBox);
        this.addRenderableWidget(maxZBox);

        // +- 操作ボタン (クリックで現在選択中の入力欄の値を素早く増減)
        this.addRenderableWidget(Button.builder(Component.literal("-16"), b -> adjustFocused(-16))
                .bounds(x + 128, y + 20, 20, 14).build());
        this.addRenderableWidget(Button.builder(Component.literal("-1"), b -> adjustFocused(-1))
                .bounds(x + 150, y + 20, 18, 14).build());
        this.addRenderableWidget(Button.builder(Component.literal("+1"), b -> adjustFocused(1))
                .bounds(x + 128, y + 36, 18, 14).build());
        this.addRenderableWidget(Button.builder(Component.literal("+16"), b -> adjustFocused(16))
                .bounds(x + 148, y + 36, 20, 14).build());

        // 範囲適用ボタン
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.apply"), b -> sendAreaUpdate())
                .bounds(x + 128, y + 52, 40, 14).build());

        // 動作切替ボタン
        toggleWorkingBtn = Button.builder(getWorkingButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_WORKING));
        }).bounds(x + 8, y + 70, 78, 18).build();
        this.addRenderableWidget(toggleWorkingBtn);

        // シルクタッチ / 幸運 切替ボタン
        toggleSilkBtn = Button.builder(getSilkButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_SILK));
        }).bounds(x + 90, y + 70, 78, 18).build();
        this.addRenderableWidget(toggleSilkBtn);

        // 液体処理切替ボタン
        toggleLiquidBtn = Button.builder(getLiquidButtonText(), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_TOGGLE_LIQUIDS));
        }).bounds(x + 8, y + 92, 78, 18).build();
        this.addRenderableWidget(toggleLiquidBtn);

        // リセットボタン
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chunkdestroyer.reset"), b -> {
            ModNetwork.CHANNEL.sendToServer(new PacketToggleState(be.getBlockPos(), PacketToggleState.ACTION_RESET_POS));
        }).bounds(x + 90, y + 92, 78, 18).build());
    }

    private EditBox createNumericBox(int x, int y, int width, int height, int initialValue) {
        EditBox box = new EditBox(this.font, x, y, width, height, Component.literal(""));
        box.setValue(String.valueOf(initialValue));
        box.setFilter(text -> text.isEmpty() || text.equals("-") || text.matches("^-?\\d+$"));
        return box;
    }

    private void adjustFocused(int delta) {
        EditBox target = lastFocusedBox != null ? lastFocusedBox : maxXBox;
        try {
            int current = Integer.parseInt(target.getValue());
            target.setValue(String.valueOf(current + delta));
            sendAreaUpdate();
        } catch (NumberFormatException ignored) {}
    }

    private void sendAreaUpdate() {
        try {
            int minX = Integer.parseInt(minXBox.getValue());
            int maxX = Integer.parseInt(maxXBox.getValue());
            int minY = Integer.parseInt(minYBox.getValue());
            int maxY = Integer.parseInt(maxYBox.getValue());
            int minZ = Integer.parseInt(minZBox.getValue());
            int maxZ = Integer.parseInt(maxZBox.getValue());

            ChunkDestroyerBlockEntity be = menu.getBlockEntity();
            ModNetwork.CHANNEL.sendToServer(new PacketUpdateArea(be.getBlockPos(), minX, maxX, minY, maxY, minZ, maxZ));
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minXBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = minXBox;
        else if (maxXBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = maxXBox;
        else if (minYBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = minYBox;
        else if (maxYBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = maxYBox;
        else if (minZBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = minZBox;
        else if (maxZBox.isMouseOver(mouseX, mouseY)) lastFocusedBox = maxZBox;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter or Keypad Enter
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
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);

        String statusText = menu.isWorking() ? "RUNNING" : "STOPPED";
        int statusColor = menu.isWorking() ? 0x228B22 : 0xB22222;
        guiGraphics.drawString(this.font, statusText, 115, 6, statusColor, false);

        guiGraphics.drawString(this.font, "X:", 8, 23, 0x404040, false);
        guiGraphics.drawString(this.font, "~", 80, 23, 0x404040, false);

        guiGraphics.drawString(this.font, "Y:", 8, 39, 0x404040, false);
        guiGraphics.drawString(this.font, "~", 80, 39, 0x404040, false);

        guiGraphics.drawString(this.font, "Z:", 8, 55, 0x404040, false);
        guiGraphics.drawString(this.font, "~", 80, 55, 0x404040, false);

        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (toggleWorkingBtn != null) toggleWorkingBtn.setMessage(getWorkingButtonText());
        if (toggleSilkBtn != null) toggleSilkBtn.setMessage(getSilkButtonText());
        if (toggleLiquidBtn != null) toggleLiquidBtn.setMessage(getLiquidButtonText());

        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
