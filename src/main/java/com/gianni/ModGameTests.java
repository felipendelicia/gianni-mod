package com.gianni;

import com.gianni.entity.LionEntity;
import com.gianni.entity.LionVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public static final Supplier<Consumer<GameTestHelper>> LION_SADDLE =
        TEST_FUNCTIONS.register("lion_saddle", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setTame(true, false);
            lion.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            if (!lion.isSaddled()) {
                helper.fail("saddle equipada pero isSaddled()=false (¿falta gianni:lion en #minecraft:can_equip_saddle?)");
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_ROAR_PANIC =
        TEST_FUNCTIONS.register("lion_roar_panic", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setVariant(LionVariant.MALE);
            Cow cow = helper.spawnMob(EntityTypes.COW, new BlockPos(3, 2, 3)).spawn();
            lion.roar();
            if (cow.getLastHurtByMob() != lion) {
                helper.fail("la vaca no entró en pánico tras el rugido");
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_STALKING =
        TEST_FUNCTIONS.register("lion_stalking", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            Cow cow = helper.spawnMob(EntityTypes.COW, new BlockPos(7, 2, 7)).spawn();
            lion.setTarget(cow);
            helper.succeedWhen(() -> {
                if (!lion.isStalking()) helper.fail("lion salvaje con target lejano no está acechando");
            });
        });

    private ModGameTests() {}
}
