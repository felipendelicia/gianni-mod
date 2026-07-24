package com.gianni;

import com.gianni.worldgen.LionDenFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
        DeferredRegister.create(Registries.FEATURE, GianniMod.MODID);

    public static final DeferredHolder<Feature<?>, LionDenFeature> LION_DEN_FEATURE =
        FEATURES.register("lion_den", LionDenFeature::new);

    private ModFeatures() {}
}
