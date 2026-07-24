package com.gianni;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = GianniMod.MODID)
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GianniMod.MODID);

    public static final DeferredBlock<Block> LION_PELT_CARPET = registerCarpet("lion_pelt_carpet");
    public static final DeferredBlock<Block> WHITE_LION_PELT_CARPET = registerCarpet("white_lion_pelt_carpet");

    private static DeferredBlock<Block> registerCarpet(String name) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(name, CarpetBlock::new,
            p -> p.strength(0.1f).sound(SoundType.WOOL));
        ModItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private ModBlocks() {}

    @SubscribeEvent
    static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(LION_PELT_CARPET);
            event.accept(WHITE_LION_PELT_CARPET);
        }
    }
}
