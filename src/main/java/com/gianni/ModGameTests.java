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
            if (!lion.isRoaringAnim()) helper.fail("roar() no activó el flag de animación de rugido");
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

    public static final Supplier<Consumer<GameTestHelper>> LION_CUB_PLAY =
        TEST_FUNCTIONS.register("lion_cub_play", () -> helper -> {
            LionEntity a = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            LionEntity b = helper.spawnMob(ModEntities.LION.get(), new BlockPos(4, 2, 4)).spawn();
            a.setBaby(true);
            b.setBaby(true);
            helper.succeedWhen(() -> {
                if (a.getNavigation().isDone() && b.getNavigation().isDone()) {
                    helper.fail("ningún cachorro se movió a jugar");
                }
            });
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_LOOT_VARIANT =
        TEST_FUNCTIONS.register("lion_loot_variant", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setVariant(LionVariant.WHITE);
            if (!com.gianni.loot.LionVariantCondition.matches(lion, LionVariant.WHITE)) {
                helper.fail("variant WHITE no matchea WHITE");
            }
            if (com.gianni.loot.LionVariantCondition.matches(lion, LionVariant.MALE)) {
                helper.fail("variant WHITE matchea MALE");
            }
            if (!com.gianni.loot.LionWildCondition.matches(lion)) {
                helper.fail("león salvaje adulto no matchea wild");
            }
            lion.setTame(true, false);
            if (com.gianni.loot.LionWildCondition.matches(lion)) {
                helper.fail("león tame matchea wild");
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_COLLAR =
        TEST_FUNCTIONS.register("lion_collar", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setTame(true, false);
            lion.setItemSlot(EquipmentSlot.BODY, new ItemStack(ModItems.FANG_COLLAR.get()));
            if (!lion.hasCollar()) {
                helper.fail("collar en BODY pero hasCollar()=false (¿tag can_wear_collar? ¿componente?)");
            }
            var rider = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
            float with = lion.getRiddenSpeed(rider);
            lion.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
            float without = lion.getRiddenSpeed(rider);
            if (with <= without) {
                helper.fail("collar no aumenta velocidad: " + with + " <= " + without);
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_DEN_PLACES =
        TEST_FUNCTIONS.register("lion_den_places", () -> helper -> {
            var level = helper.getLevel();
            var origin = helper.absolutePos(new BlockPos(8, 2, 8));
            boolean placed = com.gianni.ModFeatures.LION_DEN_FEATURE.get().place(
                net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.INSTANCE,
                level, level.getChunkSource().getGenerator(), level.getRandom(), origin);
            if (!placed) helper.fail("la feature no se colocó");
            int lions = level.getEntitiesOfClass(LionEntity.class,
                new net.minecraft.world.phys.AABB(origin).inflate(16.0)).size();
            if (lions < 2) helper.fail("guarida sin manada: " + lions + " leones");
            boolean yuyos = false;
            for (BlockPos p : BlockPos.betweenClosed(origin.offset(-10, -1, -10), origin.offset(10, 3, 10))) {
                if (level.getBlockState(p).getBlock() instanceof com.gianni.block.YuyoCropBlock) { yuyos = true; break; }
            }
            if (!yuyos) helper.fail("guarida sin yuyos silvestres");
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> YUYO_CROPS =
        TEST_FUNCTIONS.register("yuyo_crops", () -> helper -> {
            var level = helper.getLevel();
            helper.setBlock(new BlockPos(2, 1, 2), net.minecraft.world.level.block.Blocks.FARMLAND);
            helper.setBlock(new BlockPos(2, 2, 2), com.gianni.ModBlocks.YUYO_GATERO_CROP.get().defaultBlockState());
            BlockPos abs = helper.absolutePos(new BlockPos(2, 2, 2));
            for (int i = 0; i < 10 && level.getBlockState(abs).getValue(com.gianni.block.YuyoCropBlock.AGE) < com.gianni.block.YuyoCropBlock.MAX_AGE; i++) {
                var st = level.getBlockState(abs);
                ((com.gianni.block.YuyoCropBlock) st.getBlock()).performBonemeal(level, level.getRandom(), abs, st);
            }
            if (level.getBlockState(abs).getValue(com.gianni.block.YuyoCropBlock.AGE) != com.gianni.block.YuyoCropBlock.MAX_AGE) {
                helper.fail("bonemeal no maduró el yuyo gatero");
            }
            var drops = net.minecraft.world.level.block.Block.getDrops(level.getBlockState(abs), level, abs, null);
            if (drops.stream().noneMatch(s -> s.is(com.gianni.ModItems.YUYO_GATERO.get()))) {
                helper.fail("crop maduro no dropea yuyo_gatero");
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_CATNIP =
        TEST_FUNCTIONS.register("lion_catnip", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            lion.setVariant(LionVariant.MALE); // debe poder rugir para que la regresión de abajo tenga sentido
            Cow cow = helper.spawnMob(EntityTypes.COW, new BlockPos(5, 2, 5)).spawn();
            lion.setTarget(cow);
            var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(ModItems.YUYO_GATERO.get()));
            lion.mobInteract(player, net.minecraft.world.InteractionHand.MAIN_HAND);
            if (!lion.isTripping()) helper.fail("el yuyo gatero no activó el viaje");
            if (lion.getTarget() != null) helper.fail("el viaje no limpió el target");
            if (!player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).isEmpty()) helper.fail("no consumió el yuyo");
            // Regresión: un target adquirido a mitad del viaje (p.ej. por un target selector de combate)
            // no debe hacer que el león ruja mientras sigue en el viaje. roar() se dispara desde aiStep(),
            // así que hace falta dejar pasar un tick de server para que el bloque de auto-rugido corra.
            lion.setTarget(cow);
            lion.setRoarCooldownForTesting(0); // sin el gate !isTripping(), esto rugiría en el próximo tick
            helper.runAfterDelay(1, () -> {
                if (!lion.isTripping()) helper.fail("el león salió del viaje antes de tiempo");
                if (lion.isRoaringAnim()) helper.fail("el rugido de combate se disparó en pleno viaje");
                helper.succeed();
            });
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_NO_WILD_LOVE =
        TEST_FUNCTIONS.register("lion_no_wild_love", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            Cow cow = helper.spawnMob(EntityTypes.COW, new BlockPos(5, 2, 5)).spawn();
            lion.setTarget(cow); // bloquea el descanso (LionRestGoal exige target == null): no es intento de tameo
            var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(Items.BEEF));
            lion.mobInteract(player, net.minecraft.world.InteractionHand.MAIN_HAND);
            if (lion.isInLove()) helper.fail("el león salvaje entró en modo amor fuera de la ventana de tameo");
            if (player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).isEmpty()) {
                helper.fail("la carne se consumió aunque el león no podía ser tameado");
            }
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_DOPING =
        TEST_FUNCTIONS.register("lion_doping", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(1, 2, 1)).spawn();
            var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(ModItems.YUYO_DOPANTE.get()));
            lion.mobInteract(player, net.minecraft.world.InteractionHand.MAIN_HAND);
            var speed = lion.getEffect(net.minecraft.world.effect.MobEffects.SPEED);
            if (speed == null) helper.fail("sin Velocidad");
            else if (speed.getAmplifier() != 1) helper.fail("Velocidad con amplifier incorrecto: " + speed.getAmplifier());
            else if (speed.getDuration() <= 2300) helper.fail("Velocidad con duración insuficiente: " + speed.getDuration());
            var strength = lion.getEffect(net.minecraft.world.effect.MobEffects.STRENGTH);
            if (strength == null) helper.fail("sin Fuerza");
            else if (strength.getAmplifier() != 0) helper.fail("Fuerza con amplifier incorrecto: " + strength.getAmplifier());
            else if (strength.getDuration() <= 2300) helper.fail("Fuerza con duración insuficiente: " + strength.getDuration());
            var regeneration = lion.getEffect(net.minecraft.world.effect.MobEffects.REGENERATION);
            if (regeneration == null) helper.fail("sin Regeneración");
            else if (regeneration.getAmplifier() != 0) helper.fail("Regeneración con amplifier incorrecto: " + regeneration.getAmplifier());
            else if (regeneration.getDuration() <= 2300) helper.fail("Regeneración con duración insuficiente: " + regeneration.getDuration());
            if (!player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND).isEmpty()) helper.fail("no consumió el yuyo dopante");
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_BLACK_ROAR =
        TEST_FUNCTIONS.register("lion_black_roar", () -> helper -> {
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(2, 2, 2)).spawn();
            lion.setVariant(LionVariant.BLACK);
            if (lion.getVariant() != LionVariant.BLACK) helper.fail("setVariant(BLACK) no persistió");
            if (LionVariant.byId(LionVariant.BLACK.id()) != LionVariant.BLACK) helper.fail("byId no rescata BLACK");
            if (!lion.canRoar()) helper.fail("el león negro adulto debería poder rugir");
            lion.roar();
            if (!lion.isRoaringAnim()) helper.fail("roar() del negro no activó el flag de animación");
            helper.succeed();
        });

    public static final Supplier<Consumer<GameTestHelper>> LION_REST =
        TEST_FUNCTIONS.register("lion_rest", () -> helper -> {
            // El tick 1000 (de día) lo pone el test_environment gianni:day (clock_time), con teardown
            // automático — ver data/gianni/test_environment/day.json.
            LionEntity lion = helper.spawnMob(ModEntities.LION.get(), new BlockPos(2, 2, 2)).spawn();
            lion.setVariant(LionVariant.FEMALE);
            helper.runAfterDelay(10, () -> {
                if (!lion.isResting()) helper.fail("león salvaje adulto de día no descansa");
                helper.succeed();
            });
        });

    private ModGameTests() {}
}
