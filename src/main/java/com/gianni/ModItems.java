package com.gianni;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = GianniMod.MODID)
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GianniMod.MODID);

    public static final DeferredItem<Item> LION_PELT = ITEMS.registerSimpleItem("lion_pelt");
    public static final DeferredItem<Item> WHITE_LION_PELT = ITEMS.registerSimpleItem("white_lion_pelt");
    public static final DeferredItem<Item> LION_FANG = ITEMS.registerSimpleItem("lion_fang");

    private ModItems() {}

    @SubscribeEvent
    static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LION_PELT);
            event.accept(WHITE_LION_PELT);
            event.accept(LION_FANG);
        }
    }
}
