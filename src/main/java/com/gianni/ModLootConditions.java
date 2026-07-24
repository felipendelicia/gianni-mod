package com.gianni;

import com.gianni.loot.LionVariantCondition;
import com.gianni.loot.LionWildCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModLootConditions {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITIONS =
        DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, GianniMod.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<LionVariantCondition>>
        LION_VARIANT = LOOT_CONDITIONS.register("lion_variant", () -> LionVariantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<LionWildCondition>>
        LION_WILD = LOOT_CONDITIONS.register("lion_wild", () -> LionWildCondition.CODEC);

    private ModLootConditions() {}
}
