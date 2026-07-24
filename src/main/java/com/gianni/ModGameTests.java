package com.gianni;

import com.gianni.entity.LionEntity;
import com.gianni.entity.LionVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModGameTests {
    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
        DeferredRegister.create(Registries.TEST_FUNCTION, GianniMod.MODID);

    public static final Supplier<Consumer<GameTestHelper>> LION_SIT =
        TEST_FUNCTIONS.register("lion_sit", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setTame(true, false);
            lion.setOrderedToSit(true);
            helper.succeedWhen(() -> {
                if (!lion.isInSittingPose()) helper.fail(
                    "el león no entró en pose de sentado (ordered=" + lion.isOrderedToSit()
                        + " ground=" + lion.onGround() + " tame=" + lion.isTame()
                        + " pos=" + lion.position() + ")");
            });
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_BREED_VARIANT =
        TEST_FUNCTIONS.register("lion_breed_variant", () -> helper -> {
            LionEntity a = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            LionEntity b = helper.spawnMob(ModEntities.LION.get(), new BlockPos(2, 2, 1)).spawn();
            a.setVariant(LionVariant.MALE);
            b.setVariant(LionVariant.FEMALE);
            var cub = (LionEntity) a.getBreedOffspring(helper.getLevel(), b);
            if (cub == null) {
                helper.fail("getBreedOffspring devolvió null");
                return;
            }
            LionVariant v = cub.getVariant();
            if (v != LionVariant.MALE && v != LionVariant.FEMALE && v != LionVariant.WHITE) {
                helper.fail("variante inválida: " + v);
            }
            cub.discard();
            helper.succeed();
        });

    private ModGameTests() {}
}
