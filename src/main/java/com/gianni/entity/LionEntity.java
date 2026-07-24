package com.gianni.entity;

import com.gianni.ModCriteria;
import com.gianni.ModEntities;
import com.gianni.ModItems;
import com.gianni.ModSounds;
import com.gianni.entity.goal.LionAttackPlayersGoal;
import com.gianni.entity.goal.LionCubPlayGoal;
import com.gianni.entity.goal.LionHuntPreyGoal;
import com.gianni.entity.goal.LionRestGoal;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class LionEntity extends TamableAnimal implements PlayerRideableJumping {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
        SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_RESTING =
        SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STALKING =
        SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);

    public LionEntity(EntityType<? extends LionEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.STEP_HEIGHT, 1.25);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, LionVariant.FEMALE.id());
        builder.define(DATA_RESTING, false);
        builder.define(DATA_STALKING, false);
    }

    public LionVariant getVariant() { return LionVariant.byId(this.entityData.get(DATA_VARIANT)); }
    public void setVariant(LionVariant variant) { this.entityData.set(DATA_VARIANT, variant.id()); }
    public boolean isResting() { return this.entityData.get(DATA_RESTING); }
    public void setResting(boolean resting) { this.entityData.set(DATA_RESTING, resting); }
    public boolean isStalking() { return this.entityData.get(DATA_STALKING); }

    public boolean isDaytime() {
        return this.level().isBrightOutside();
    }

    private int roarCooldown;          // ticks; no persiste
    private boolean roaredTonight;

    public boolean canRoar() {
        return this.getVariant() == LionVariant.MALE && !this.isBaby();
    }

    public void roar() {
        if (this.level().isClientSide()) return;
        this.playSound(ModSounds.LION_ROAR.get(), 2.0f, 1.0f);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CLOUD,
                this.getX(), this.getEyeY(), this.getZ(), 8, 0.5, 0.3, 0.5, 0.02);
            for (Mob prey : server.getEntitiesOfClass(Mob.class,
                    this.getBoundingBox().inflate(16.0),
                    e -> LionHuntPreyGoal.PREY.contains(e.getType()))) {
                prey.setLastHurtByMob(this);   // dispara PanicGoal vanilla sin daño
            }
        }
        this.roarCooldown = 1200;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.canRoar()) {
            if (this.roarCooldown > 0) this.roarCooldown--;
            boolean night = !this.isDaytime();
            if (!night) {
                this.roaredTonight = false;
            } else if (!this.roaredTonight) {
                this.roaredTonight = true;
                this.roar();
            }
            if (this.roarCooldown == 0 && this.getTarget() != null && this.getTarget().isAlive()) {
                this.roar();
            }
        }
        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            this.entityData.set(DATA_STALKING,
                !this.isTame() && target != null && target.isAlive()
                    && this.distanceToSqr(target) > 16.0);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("Variant", this.getVariant().id());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        this.setVariant(LionVariant.byId(in.getIntOr("Variant", 0)));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        float r = level.getRandom().nextFloat();
        this.setVariant(r < 0.05f ? LionVariant.WHITE : r < 0.45f ? LionVariant.MALE : LionVariant.FEMALE);
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LionRestGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.1, 10.0f, 2.0f));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LionCubPlayGoal(this, 1.2));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new LionAttackPlayersGoal(this));
        this.targetSelector.addGoal(5, new LionHuntPreyGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() { return ModSounds.LION_AMBIENT.get(); }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.LION_HURT.get(); }

    @Override
    protected SoundEvent getDeathSound() { return ModSounds.LION_DEATH.get(); }

    public static final Set<Item> TAMING_FOOD = Set.of(
        Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON, Items.RABBIT);

    @Override
    public boolean isFood(ItemStack stack) {
        return TAMING_FOOD.contains(stack.getItem());
    }

    public boolean canBeTamed() {
        return this.isBaby() || (this.isDaytime() && this.isResting());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isTame() && this.isOwnedBy(player)) {
            if (stack.is(ModItems.FANG_COLLAR.get()) && !this.isBaby() && !this.hasCollar()) {
                if (!this.level().isClientSide()) {
                    this.setItemSlot(EquipmentSlot.BODY, stack.consumeAndReturn(1, player));
                    this.setGuaranteedDrop(EquipmentSlot.BODY);
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(Items.SADDLE) && !this.isBaby() && !this.isSaddled()) {
                if (!this.level().isClientSide()) {
                    this.setItemSlot(EquipmentSlot.SADDLE, stack.consumeAndReturn(1, player));
                    this.setGuaranteedDrop(EquipmentSlot.SADDLE);
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.isEmpty() && this.isSaddled() && !this.isBaby() && !player.isShiftKeyDown()) {
                if (!this.level().isClientSide()) {
                    this.setOrderedToSit(false);
                    player.startRiding(this);
                }
                return InteractionResult.SUCCESS;
            }
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth() && !this.isBaby()) {
                stack.consume(1, player);
                this.heal(4.0f);
                return InteractionResult.SUCCESS;
            }
            if (stack.isEmpty() && !this.isVehicle()) {
                if (!this.level().isClientSide()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                }
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, hand); // deja que Animal maneje el modo amor (cría)
        }
        if (!this.isTame() && this.isFood(stack) && this.canBeTamed()) {
            stack.consume(1, player);
            if (!this.level().isClientSide()) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                } else {
                    this.level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            this.setHealth(40.0f);
        }
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.SADDLE || slot == EquipmentSlot.BODY) {
            return this.isAlive() && this.isTame() && !this.isBaby();
        }
        return super.canUseSlot(slot);
    }

    public boolean hasCollar() {
        return this.isWearingBodyArmor();
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        if (this.getVariant() == LionVariant.WHITE && player instanceof ServerPlayer sp) {
            ModCriteria.TAMED_WHITE_LION.get().trigger(sp);
        }
    }

    @Override
    protected Holder<SoundEvent> getEquipSound(EquipmentSlot slot, ItemStack stack, Equippable equippable) {
        return slot == EquipmentSlot.SADDLE ? SoundEvents.HORSE_SADDLE : super.getEquipSound(slot, stack, equippable);
    }

    private float playerJumpPendingScale;

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled() && this.getFirstPassenger() instanceof Player p && this.isOwnedBy(p)) {
            return p;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5f, 0.0, player.zza >= 0.0f ? player.zza : player.zza * 0.25f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.onGround() && this.playerJumpPendingScale > 0.0f) {
            Vec3 dm = this.getDeltaMovement();
            this.setDeltaMovement(dm.x, 0.7 * this.playerJumpPendingScale, dm.z);
            this.playerJumpPendingScale = 0.0f;
        }
    }

    @Override
    public float getRiddenSpeed(Player player) {
        float base = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.6f;
        return this.hasCollar() ? base * 1.3f : base;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        this.playerJumpPendingScale = jumpPower >= 90 ? 1.0f : 0.4f + 0.4f * jumpPower / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.playSound(SoundEvents.POLAR_BEAR_WARNING);
    }

    @Override
    public void handleStopJump() {}

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        LionEntity cub = ModEntities.LION.get().create(level, EntitySpawnReason.BREEDING);
        if (cub == null) return null;
        LionVariant inherited = this.random.nextBoolean() || !(partner instanceof LionEntity other)
            ? this.getVariant()
            : other.getVariant();
        if (this.random.nextFloat() < 0.05f) {
            inherited = LionVariant.WHITE;
        }
        cub.setVariant(inherited);
        return cub;
    }
}
