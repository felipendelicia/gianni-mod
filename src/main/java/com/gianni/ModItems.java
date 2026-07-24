package com.gianni;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
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

    public static final TagKey<EntityType<?>> CAN_WEAR_COLLAR =
        TagKey.create(Registries.ENTITY_TYPE, GianniMod.id("can_wear_collar"));

    public static final DeferredItem<Item> FANG_COLLAR = ITEMS.registerItem("fang_collar",
        props -> new Item(props.stacksTo(1).component(DataComponents.EQUIPPABLE,
            Equippable.builder(EquipmentSlot.BODY)
                .setEquipSound(SoundEvents.HORSE_SADDLE)
                .setAllowedEntities(BuiltInRegistries.acquireBootstrapRegistrationLookup(
                    BuiltInRegistries.ENTITY_TYPE).getOrThrow(CAN_WEAR_COLLAR))
                .setEquipOnInteract(false)
                .build())));

    private ModItems() {}

    @SubscribeEvent
    static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LION_PELT);
            event.accept(WHITE_LION_PELT);
            event.accept(LION_FANG);
            event.accept(FANG_COLLAR);
        }
    }
}
