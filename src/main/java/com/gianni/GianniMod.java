package com.gianni;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(GianniMod.MODID)
public class GianniMod {
    public static final String MODID = "gianni";

    public GianniMod(IEventBus modEventBus, ModContainer modContainer) {
        // Los DeferredRegister de ModEntities se registran acá (Task 2).
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
