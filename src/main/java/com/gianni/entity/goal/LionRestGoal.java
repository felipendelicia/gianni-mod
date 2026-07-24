package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LionRestGoal extends Goal {
    private final LionEntity lion;

    public LionRestGoal(LionEntity lion) {
        this.lion = lion;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return !lion.isTame() && lion.isDaytime() && lion.getTarget() == null && !lion.isInLove();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        lion.getNavigation().stop();
        lion.setResting(true);
    }

    @Override
    public void stop() {
        lion.setResting(false);
    }
}
