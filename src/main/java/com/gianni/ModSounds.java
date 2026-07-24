package com.gianni;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
        DeferredRegister.create(Registries.SOUND_EVENT, GianniMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LION_AMBIENT = register("lion_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LION_ROAR = register("lion_roar");
    public static final DeferredHolder<SoundEvent, SoundEvent> LION_HURT = register("lion_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LION_DEATH = register("lion_death");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(GianniMod.id(name)));
    }

    private ModSounds() {}
}
