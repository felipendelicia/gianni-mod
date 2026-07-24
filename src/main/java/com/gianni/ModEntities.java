package com.gianni;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.entity.SpawnPlacementTypes;

@EventBusSubscriber(modid = GianniMod.MODID)
public final class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(GianniMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GianniMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<LionEntity>> LION =
        ENTITY_TYPES.registerEntityType("lion", LionEntity::new, MobCategory.CREATURE,
            b -> b.sized(1.3f, 1.1f).eyeHeight(0.95f).clientTrackingRange(10));

    public static final DeferredItem<SpawnEggItem> LION_SPAWN_EGG =
        ITEMS.registerItem("lion_spawn_egg", props -> new SpawnEggItem(props.spawnEgg(LION.get())));

    private ModEntities() {}

    @SubscribeEvent
    static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(LION.get(), LionEntity.createAttributes().build());
    }

    @SubscribeEvent
    static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(LION.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules,
            RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(LION_SPAWN_EGG);
        }
    }
}
