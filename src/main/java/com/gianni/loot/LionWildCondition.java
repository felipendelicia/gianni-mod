package com.gianni.loot;

import com.gianni.ModLootConditions;
import com.gianni.entity.LionEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

public record LionWildCondition() implements LootItemCondition {
    public static final MapCodec<LionWildCondition> CODEC = MapCodec.unit(new LionWildCondition());

    public static boolean matches(@Nullable Entity entity) {
        return entity instanceof LionEntity lion && !lion.isTame() && !lion.isBaby();
    }

    @Override
    public boolean test(LootContext context) {
        return matches(context.getOptionalParameter(LootContextParams.THIS_ENTITY));
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return ModLootConditions.LION_WILD.get();
    }
}
