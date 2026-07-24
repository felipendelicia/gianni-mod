package com.gianni;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, GianniMod.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> YUYO =
        PARTICLES.register("yuyo", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
