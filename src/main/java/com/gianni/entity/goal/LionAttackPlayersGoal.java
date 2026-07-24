package com.gianni.entity.goal;

import com.gianni.entity.LionEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class LionAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
    private final LionEntity lion;

    public LionAttackPlayersGoal(LionEntity lion) {
        super(lion, Player.class, true);
        this.lion = lion;
    }

    @Override
    public boolean canUse() {
        return !lion.isTame() && !lion.isBaby() && !lion.isDaytime() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !lion.isTame() && super.canContinueToUse();
    }
}
