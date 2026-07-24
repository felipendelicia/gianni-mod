package com.gianni.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

// ADAPT: en fuentes 26.2 `TextureSheetParticle` no existe; la clase base pasó a ser
// `SingleQuadParticle` (net.minecraft.client.particle), cuyo constructor recibe el
// TextureAtlasSprite directamente (no hay `pickSprite`/`Particle.pickSprite`).
// `getRenderType()` pasó a ser `getLayer()` (devuelve `SingleQuadParticle.Layer`, no
// `ParticleRenderType`). `ParticleProvider#createParticle` ahora recibe un `RandomSource`
// final además de las velocidades. Verificado contra SingleQuadParticle.java, Particle.java,
// SuspendedParticle.java y HeartParticle.java desofuscados.
public class YuyoParticle extends SingleQuadParticle {
    protected YuyoParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
            TextureAtlasSprite sprite) {
        super(level, x, y, z, vx, vy, vz, sprite);
        this.lifetime = 30 + this.random.nextInt(20);
        this.gravity = -0.02f;                    // flota
        this.xd = vx; this.yd = vy; this.zd = vz; // cancela el spread aleatorio que aplica Particle(...)
        this.quadSize = 0.08f + this.random.nextFloat() * 0.04f;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() { return SingleQuadParticle.Layer.TRANSLUCENT; }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - (float) this.age / this.lifetime;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
            return new YuyoParticle(level, x, y, z, vx, vy, vz, this.sprites.get(random));
        }
    }
}
