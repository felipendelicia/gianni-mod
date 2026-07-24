package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class LionCubPlayGoal extends Goal {
    private final LionEntity cub;
    private final double speed;
    private LionEntity playmate;
    private int playTicks;

    public LionCubPlayGoal(LionEntity cub, double speed) {
        this.cub = cub;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!cub.isBaby() || cub.getTarget() != null || cub.isInWater()) return false;
        if (cub.getRandom().nextInt(reducedTickDelay(60)) != 0) return false;
        List<LionEntity> cubs = cub.level().getEntitiesOfClass(
            LionEntity.class, cub.getBoundingBox().inflate(8.0),
            other -> other != cub && other.isBaby() && other.isAlive());
        if (cubs.isEmpty()) return false;
        this.playmate = cubs.get(cub.getRandom().nextInt(cubs.size()));
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return playTicks > 0 && playmate.isAlive() && cub.getTarget() == null;
    }

    @Override
    public void start() {
        this.playTicks = reducedTickDelay(120);
    }

    @Override
    public void tick() {
        playTicks--;
        cub.getNavigation().moveTo(playmate, speed);
        if (cub.distanceToSqr(playmate) < 4.0 && cub.onGround() && cub.getRandom().nextInt(5) == 0) {
            cub.getJumpControl().jump();
        }
    }

    @Override
    public void stop() {
        this.playmate = null;
        cub.getNavigation().stop();
    }
}
