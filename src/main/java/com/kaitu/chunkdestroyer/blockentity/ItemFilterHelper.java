package com.kaitu.chunkdestroyer.blockentity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;

public class ItemFilterHelper {
    public static final TagKey<Item> FORGE_COBBLESTONE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "cobblestone"));
    public static final TagKey<Item> FORGE_STONE = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "stone"));
    public static final TagKey<Item> FORGE_GRAVEL = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "gravel"));
    public static final TagKey<Item> FORGE_SAND = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "sand"));

    private static final Set<Item> STONE_ITEMS = Set.of(
            Items.COBBLESTONE,
            Items.STONE,
            Items.DEEPSLATE,
            Items.COBBLED_DEEPSLATE,
            Items.ANDESITE,
            Items.DIORITE,
            Items.GRANITE,
            Items.TUFF,
            Items.BASALT,
            Items.SMOOTH_BASALT,
            Items.BLACKSTONE,
            Items.NETHERRACK,
            Items.END_STONE,
            Items.SANDSTONE,
            Items.RED_SANDSTONE,
            Items.CALCITE,
            Items.DRIPSTONE_BLOCK
    );

    private static final Set<Item> DIRT_ITEMS = Set.of(
            Items.DIRT,
            Items.COARSE_DIRT,
            Items.GRASS_BLOCK,
            Items.PODZOL,
            Items.MYCELIUM,
            Items.ROOTED_DIRT,
            Items.MUD,
            Items.MUDDY_MANGROVE_ROOTS,
            Items.GRAVEL,
            Items.SAND,
            Items.RED_SAND,
            Items.CLAY_BALL,
            Items.CLAY
    );

    public static boolean isStone(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (STONE_ITEMS.contains(item)) return true;
        try {
            if (stack.is(FORGE_COBBLESTONE) || stack.is(FORGE_STONE)) return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean isDirt(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (DIRT_ITEMS.contains(item)) return true;
        try {
            if (stack.is(ItemTags.DIRT) || stack.is(ItemTags.SAND) || stack.is(FORGE_GRAVEL) || stack.is(FORGE_SAND)) return true;
        } catch (Exception ignored) {}
        return false;
    }
}
