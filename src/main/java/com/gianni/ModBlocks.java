package com.gianni;

import com.gianni.block.YuyoCropBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = GianniMod.MODID)
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GianniMod.MODID);

    public static final DeferredBlock<Block> LION_PELT_CARPET = registerCarpet("lion_pelt_carpet");
    public static final DeferredBlock<Block> WHITE_LION_PELT_CARPET = registerCarpet("white_lion_pelt_carpet");

    public static final DeferredBlock<Block> YUYO_GATERO_CROP = registerYuyo("yuyo_gatero_crop", () -> ModItems.YUYO_GATERO.get());
    public static final DeferredBlock<Block> YUYO_DOPANTE_CROP = registerYuyo("yuyo_dopante_crop", () -> ModItems.YUYO_DOPANTE.get());

    private static DeferredBlock<Block> registerCarpet(String name) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(name, CarpetBlock::new,
            p -> p.strength(0.1f).sound(SoundType.WOOL));
        ModItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static DeferredBlock<Block> registerYuyo(String name, Supplier<? extends ItemLike> seed) {
        // ADAPT: verificado contra BlockBehaviour.java desofuscado (26.2): el método se
        // renombró de noCollission() (con doble s) a noCollision() (ortografía corregida).
        return BLOCKS.registerBlock(name, p -> new YuyoCropBlock(seed, p),
            p -> p.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak()
                  .sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
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
