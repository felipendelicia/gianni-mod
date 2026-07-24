package com.gianni.worldgen;

import com.gianni.ModEntities;
import com.gianni.entity.LionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LionDenFeature extends Feature<NoneFeatureConfiguration> {

    public LionDenFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();

        // planchón 12x12 orgánico, 2 capas
        for (int layer = 0; layer < 2; layer++) {
            int radius = 6 - layer * 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > radius - random.nextDouble() * 1.5) continue;
                    BlockPos p = origin.offset(dx, layer, dz);
                    var block = random.nextInt(4) == 0 ? Blocks.SANDSTONE : Blocks.SMOOTH_SANDSTONE;
                    level.setBlock(p, block.defaultBlockState(), 3);
                }
            }
        }
        // huesos incrustados
        int bones = 3 + random.nextInt(3);
        for (int i = 0; i < bones; i++) {
            BlockPos p = origin.offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
            level.setBlock(p, Blocks.BONE_BLOCK.defaultBlockState(), 3);
        }
        // arbustos secos alrededor
        for (int i = 0; i < 5; i++) {
            BlockPos p = origin.offset(random.nextInt(15) - 7, 0, random.nextInt(15) - 7);
            if (level.isEmptyBlock(p.above(1)) && level.getBlockState(p).isSolid()) {
                level.setBlock(p.above(1), Blocks.DEAD_BUSH.defaultBlockState(), 3);
            }
        }
        // manada 2-4
        int count = 2 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            LionEntity lion = ModEntities.LION.get().create(level.getLevel(), EntitySpawnReason.CHUNK_GENERATION);
            if (lion == null) continue;
            lion.snapTo(origin.getX() + random.nextInt(7) - 3 + 0.5,
                origin.getY() + 2, origin.getZ() + random.nextInt(7) - 3 + 0.5,
                random.nextFloat() * 360f, 0f);
            lion.finalizeSpawn(level, level.getCurrentDifficultyAt(origin),
                EntitySpawnReason.CHUNK_GENERATION, null);
            level.addFreshEntity(lion);
        }
        return true;
    }
}
