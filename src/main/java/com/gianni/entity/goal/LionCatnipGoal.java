package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LionCatnipGoal extends Goal {
    private final LionEntity lion;
    private int tick;

    public LionCatnipGoal(LionEntity lion) {
        this.lion = lion;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override public boolean canUse() { return lion.isTripping(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void start() { this.tick = 0; }

    @Override
    public void tick() {
        tick++;
        int phase = (tick / 60) % 3;             // 3 s por fase: rodar, círculos, saltitos
        if (phase == 0) {
            lion.getNavigation().stop();
            lion.setRolling(true);
        } else {
            lion.setRolling(false);
            if (phase == 1) {
                if (lion.getNavigation().isDone()) {
                    double ang = (tick % 120) / 120.0 * Math.PI * 2;
                    lion.getNavigation().moveTo(lion.getX() + Math.cos(ang) * 4.0, lion.getY(),
                        lion.getZ() + Math.sin(ang) * 4.0, 1.3);
                }
            } else if (lion.onGround() && lion.getRandom().nextInt(10) == 0) {
                lion.getJumpControl().jump();
            }
        }
    }

    @Override
    public void stop() {
        lion.setRolling(false);
        lion.getNavigation().stop();
    }
}
