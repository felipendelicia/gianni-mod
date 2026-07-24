package com.gianni.loot;

import com.gianni.ModLootConditions;
import com.gianni.entity.LionEntity;
import com.gianni.entity.LionVariant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public record LionVariantCondition(LionVariant variant) implements LootItemCondition {
    private static final Codec<LionVariant> VARIANT_CODEC = Codec.STRING.xmap(
        s -> LionVariant.valueOf(s.toUpperCase(Locale.ROOT)),
        v -> v.name().toLowerCase(Locale.ROOT));
    public static final MapCodec<LionVariantCondition> CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(VARIANT_CODEC.fieldOf("variant").forGetter(LionVariantCondition::variant))
            .apply(i, LionVariantCondition::new));

    public static boolean matches(@Nullable Entity entity, LionVariant variant) {
        return entity instanceof LionEntity lion && lion.getVariant() == variant;
    }

    @Override
    public boolean test(LootContext context) {
        return matches(context.getOptionalParameter(LootContextParams.THIS_ENTITY), this.variant);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return ModLootConditions.LION_VARIANT.get();
    }
}
