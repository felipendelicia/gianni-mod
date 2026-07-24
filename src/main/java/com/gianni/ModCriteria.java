package com.gianni;

import com.gianni.advancement.TamedWhiteLionTrigger;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCriteria {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
        DeferredRegister.create(Registries.TRIGGER_TYPE, GianniMod.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, TamedWhiteLionTrigger> TAMED_WHITE_LION =
        TRIGGERS.register("tamed_white_lion", TamedWhiteLionTrigger::new);

    private ModCriteria() {}
}
