package com.kaitu.chunkdestroyer;

import com.kaitu.chunkdestroyer.block.ChunkDestroyerBlock;
import com.kaitu.chunkdestroyer.blockentity.ChunkDestroyerBlockEntity;
import com.kaitu.chunkdestroyer.client.ChunkDestroyerScreen;
import com.kaitu.chunkdestroyer.config.ModConfig;
import com.kaitu.chunkdestroyer.menu.ChunkDestroyerMenu;
import com.kaitu.chunkdestroyer.network.ModNetwork;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ChunkDestroyerMod.MOD_ID)
public class ChunkDestroyerMod {
    public static final String MOD_ID = "chunkdestroyer";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<ChunkDestroyerBlock> CHUNK_DESTROYER_BLOCK = BLOCKS.register("chunk_destroyer",
            ChunkDestroyerBlock::new);

    public static final RegistryObject<Item> CHUNK_DESTROYER_ITEM = ITEMS.register("chunk_destroyer",
            () -> new BlockItem(CHUNK_DESTROYER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<ChunkDestroyerBlockEntity>> CHUNK_DESTROYER_BE = BLOCK_ENTITIES.register("chunk_destroyer",
            () -> BlockEntityType.Builder.of(ChunkDestroyerBlockEntity::new, CHUNK_DESTROYER_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<ChunkDestroyerMenu>> CHUNK_DESTROYER_MENU = MENU_TYPES.register("chunk_destroyer",
            () -> IForgeMenuType.create(ChunkDestroyerMenu::new));

    public static final RegistryObject<CreativeModeTab> CHUNK_DESTROYER_TAB = CREATIVE_MODE_TABS.register("chunk_destroyer_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.chunkdestroyer"))
                    .icon(() -> new ItemStack(CHUNK_DESTROYER_ITEM.get()))
                    .displayItems((params, output) -> output.accept(CHUNK_DESTROYER_ITEM.get()))
                    .build());

    public ChunkDestroyerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC, "chunkdestroyer-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CHUNK_DESTROYER_TAB.getKey()) {
            event.accept(CHUNK_DESTROYER_ITEM.get());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> MenuScreens.register(CHUNK_DESTROYER_MENU.get(), ChunkDestroyerScreen::new));
        }
    }
}
