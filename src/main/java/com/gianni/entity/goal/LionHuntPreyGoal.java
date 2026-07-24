package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;

import java.util.Set;

public class LionHuntPreyGoal extends NearestAttackableTargetGoal<Animal> {
    private static final Set<EntityType<?>> PREY = Set.of(
        EntityTypes.COW, EntityTypes.SHEEP, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.RABBIT);
    private final LionEntity lion;

    public LionHuntPreyGoal(LionEntity lion) {
        super(lion, Animal.class, 10, true, false, (target, level) -> PREY.contains(target.getType()));
        this.lion = lion;
    }

    @Override
    public boolean canUse() {
        return !lion.isTame() && !lion.isDaytime() && super.canUse();
    }
}
