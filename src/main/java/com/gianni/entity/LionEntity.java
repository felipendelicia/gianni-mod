package com.gianni.entity;

import com.gianni.entity.goal.LionAttackPlayersGoal;
import com.gianni.entity.goal.LionHuntPreyGoal;
import com.gianni.entity.goal.LionRestGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class LionEntity extends TamableAnimal {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
        SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_RESTING =
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
    }

    public LionVariant getVariant() { return LionVariant.byId(this.entityData.get(DATA_VARIANT)); }
    public void setVariant(LionVariant variant) { this.entityData.set(DATA_VARIANT, variant.id()); }
    public boolean isResting() { return this.entityData.get(DATA_RESTING); }
    public void setResting(boolean resting) { this.entityData.set(DATA_RESTING, resting); }

    public boolean isDaytime() {
        return this.level().isBrightOutside();
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
    protected SoundEvent getAmbientSound() { return SoundEvents.POLAR_BEAR_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.POLAR_BEAR_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.POLAR_BEAR_DEATH; }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // Task 6 define la comida real
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Task 8
    }
}
