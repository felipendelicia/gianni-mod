package com.gianni.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class YuyoCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    // ADAPT: verificado contra CropBlock.java desofuscado (26.2): codec() es
    // `public MapCodec<? extends CropBlock> codec()` (no abstracto en CropBlock,
    // pero VegetationBlock lo declara abstracto), así que cada bloque necesita su
    // propio CODEC. Usamos un supplier fijo (WHEAT_SEEDS) porque el codec no se
    // usa para diferenciar gatero/dopante en runtime (el seed real llega por el
    // constructor vía DeferredRegister, no por deserialización del codec).
    public static final MapCodec<YuyoCropBlock> CODEC = simpleCodec(p -> new YuyoCropBlock(() -> net.minecraft.world.item.Items.WHEAT_SEEDS, p));

    private final Supplier<? extends ItemLike> seed;

    public YuyoCropBlock(Supplier<? extends ItemLike> seed, Properties props) {
        super(props);
        this.seed = seed;
    }

    @Override public MapCodec<? extends CropBlock> codec() { return CODEC; }
    @Override public IntegerProperty getAgeProperty() { return AGE; }
    @Override public int getMaxAge() { return MAX_AGE; }
    @Override protected ItemLike getBaseSeedId() { return seed.get(); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }
}
