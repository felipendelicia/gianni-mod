package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LionRestGoal extends Goal {
    private final LionEntity lion;
    private long restUntil;        // tickCount hasta el que descansa
    private long cooldownUntil;    // tickCount hasta el que no puede volver a descansar

    public LionRestGoal(LionEntity lion) {
        this.lion = lion;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    private boolean conditions() {
        return !lion.isTame() && !lion.isBaby() && lion.isDaytime() && lion.getTarget() == null && !lion.isInLove();
    }

    @Override
    public boolean canUse() {
        return lion.tickCount >= this.cooldownUntil && conditions();
    }

    @Override
    public boolean canContinueToUse() {
        return lion.tickCount < this.restUntil && conditions();
    }

    @Override
    public void start() {
        this.restUntil = lion.tickCount + 300 + lion.getRandom().nextInt(501);      // 15-40 s
        lion.getNavigation().stop();
        lion.setResting(true);
    }

    @Override
    public void stop() {
        this.cooldownUntil = lion.tickCount + 200 + lion.getRandom().nextInt(401);  // 10-30 s
        lion.setResting(false);
    }
}
