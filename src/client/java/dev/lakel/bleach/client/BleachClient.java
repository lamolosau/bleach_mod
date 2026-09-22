package dev.lakel.bleach.client;

import dev.lakel.bleach.BleachMod;
import dev.lakel.bleach.client.renderer.FishboneRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class BleachClient implements ClientModInitializer {

    private static int currentReiatsuFrame = 9;
    private static int regenTimer = 0;
    private static boolean wasAttackPressed = false;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BleachMod.FISHBONE, FishboneRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean isAttackPressed = client.options.keyAttack.isDown();
            if (isAttackPressed && !wasAttackPressed) {
                if (currentReiatsuFrame > 0) {
                    currentReiatsuFrame--;
                }
            }
            wasAttackPressed = isAttackPressed;

            if (currentReiatsuFrame < 9) {
                regenTimer++;
                if (regenTimer >= 200) {
                    currentReiatsuFrame++;
                    regenTimer = 0;
                }
            } else {
                regenTimer = 0;
            }
        });

        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            
            if (client.player != null && !client.player.isSpectator()) {
                ResourceLocation texture = new ResourceLocation("bleach_mod", "textures/gui/reiatsu_bar.png");
                
                int width = client.getWindow().getGuiScaledWidth();
                int height = client.getWindow().getGuiScaledHeight();
                
                int x = (width / 2) - 91;
                int y = height - 54; 
                
                int v = currentReiatsuFrame * 10;
                
                guiGraphics.blit(texture, x, y, 0, v, 182, 10, 182, 100);
            }
        });
    }
}
