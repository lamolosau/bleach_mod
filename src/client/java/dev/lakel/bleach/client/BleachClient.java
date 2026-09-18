package dev.lakel.bleach.client;

import dev.lakel.bleach.BleachMod;
import dev.lakel.bleach.client.renderer.FishboneRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class BleachClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BleachMod.FISHBONE, FishboneRenderer::new);
    }
}
