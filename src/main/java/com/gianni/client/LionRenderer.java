package com.gianni.client;

import com.gianni.GianniMod;
import com.gianni.entity.LionEntity;
import com.gianni.entity.LionVariant;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class LionRenderer extends AgeableMobRenderer<LionEntity, LionRenderState, LionModel> {
    private static final Identifier TEX_MALE = GianniMod.id("textures/entity/lion/lion_male.png");
    private static final Identifier TEX_FEMALE = GianniMod.id("textures/entity/lion/lion_female.png");
    private static final Identifier TEX_WHITE = GianniMod.id("textures/entity/lion/lion_white.png");
    private static final Identifier TEX_BLACK = GianniMod.id("textures/entity/lion/lion_black.png");

    public LionRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LionModel(ctx.bakeLayer(GianniModClient.LION)),
                   new LionModel(ctx.bakeLayer(GianniModClient.LION_BABY)), 0.7f);
        this.addLayer(new LionEyesLayer(this));
    }

    @Override
    public LionRenderState createRenderState() {
        return new LionRenderState();
    }

    @Override
    public void extractRenderState(LionEntity lion, LionRenderState s, float partialTick) {
        super.extractRenderState(lion, s, partialTick);
        s.male = lion.getVariant().isMaleVariant();
        s.resting = lion.isResting();
        s.sitting = lion.isInSittingPose();
        s.saddled = lion.isSaddled();
        s.stalking = lion.isStalking();
        s.collared = lion.hasCollar();
        s.rolling = lion.isRolling();
        s.tripping = lion.isTripping();
        s.roaring = lion.isRoaringAnim();
        s.nightEyes = lion.level().isDarkOutside() || lion.isStalking();
        s.texture = switch (lion.getVariant()) {
            case MALE -> TEX_MALE;
            case WHITE -> TEX_WHITE;
            case BLACK -> TEX_BLACK;
            default -> TEX_FEMALE;
        };
    }

    @Override
    public Identifier getTextureLocation(LionRenderState s) {
        return s.texture;
    }
}
