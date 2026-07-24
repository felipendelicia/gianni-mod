package com.gianni.client;

import com.gianni.GianniMod;
import com.gianni.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = GianniMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GianniMod.MODID, value = Dist.CLIENT)
public class GianniModClient {
    public static final ModelLayerLocation LION = new ModelLayerLocation(GianniMod.id("lion"), "main");
    public static final ModelLayerLocation LION_BABY = new ModelLayerLocation(GianniMod.id("lion"), "baby");

    @SubscribeEvent
    static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LION, LionModel::createBodyLayer);
        event.registerLayerDefinition(LION_BABY, LionModel::createBabyLayer);
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LION.get(), LionRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterParticles(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(com.gianni.ModParticles.YUYO.get(), YuyoParticle.Provider::new);
    }
}
