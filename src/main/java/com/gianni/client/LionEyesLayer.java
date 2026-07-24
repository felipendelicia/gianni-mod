package com.gianni.client;

import com.gianni.GianniMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class LionEyesLayer extends EyesLayer<LionRenderState, LionModel> {
    private static final RenderType LION_EYES =
        RenderTypes.eyes(GianniMod.id("textures/entity/lion/lion_eyes.png"));

    public LionEyesLayer(RenderLayerParent<LionRenderState, LionModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
            LionRenderState state, float yRot, float xRot) {
        if (!state.nightEyes) return;
        super.submit(poseStack, submitNodeCollector, lightCoords, state, yRot, xRot);
    }

    @Override
    public RenderType renderType() {
        return LION_EYES;
    }
}
