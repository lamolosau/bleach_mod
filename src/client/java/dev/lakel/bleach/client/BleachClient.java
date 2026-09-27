package dev.lakel.bleach.client;

import dev.lakel.bleach.BleachMod;
import dev.lakel.bleach.client.renderer.FishboneRenderer;
import dev.lakel.bleach.item.AsauchiItem;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.VariableIntPower;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.client.RenderProvider;

public class BleachClient implements ClientModInitializer {

    private static float targetReiatsu = 100f;
    private static float animatedReiatsu = 100f;
    private static float maxReiatsu = 100f;

    private static final ResourceLocation REIATSU_BAR_TEXTURE = new ResourceLocation("bleach_mod", "textures/gui/reiatsu_bar.png");
    private static final ResourceLocation REIATSU_POWER_ID = new ResourceLocation("bleach_mod", "reiatsu_resource");
    
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 5;
    private static final int TEXTURE_WIDTH = 81;
    private static final int TEXTURE_HEIGHT = 10;

    @Override
    public void onInitializeClient() {
        
        EntityRendererRegistry.register(BleachMod.INERT_BODY, InertBodyRenderer::new);
        EntityRendererRegistry.register(BleachMod.FISHBONE, FishboneRenderer::new);

        AsauchiItem.CLIENT_RENDER_PROVIDER = () -> new RenderProvider() {
            private dev.lakel.bleach.client.renderer.AsauchiRenderer renderer;
            
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new dev.lakel.bleach.client.renderer.AsauchiRenderer();
                }
                return this.renderer;
            }
        };

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (PowerTypeRegistry.contains(REIATSU_POWER_ID)) {
                PowerType<?> powerType = PowerTypeRegistry.get(REIATSU_POWER_ID);
                PowerHolderComponent component = PowerHolderComponent.KEY.get(client.player);
                
                if (component.hasPower(powerType)) {
                    io.github.apace100.apoli.power.Power power = component.getPower(powerType);
                    if (power instanceof VariableIntPower resourcePower) {
                        targetReiatsu = resourcePower.getValue();
                        maxReiatsu = resourcePower.getMax(); 
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();

            if (client.player != null && !client.player.isSpectator()) {
                
                if (!PowerTypeRegistry.contains(REIATSU_POWER_ID)) return;
                PowerType<?> powerType = PowerTypeRegistry.get(REIATSU_POWER_ID);
                
                if (!PowerHolderComponent.KEY.get(client.player).hasPower(powerType)) {
                    return; 
                }

                int screenWidth = client.getWindow().getGuiScaledWidth();
                int screenHeight = client.getWindow().getGuiScaledHeight();

                int x = (screenWidth / 2) + 10;
                int y = screenHeight - 54; 

                animatedReiatsu = Mth.lerp(tickDelta * 0.1f, animatedReiatsu, targetReiatsu);
                
                float percentage = Math.max(0, Math.min(1f, animatedReiatsu / maxReiatsu));
                int drawnWidth = (int) (BAR_WIDTH * percentage);

                guiGraphics.blit(REIATSU_BAR_TEXTURE, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                if (drawnWidth > 0) {
                    guiGraphics.blit(REIATSU_BAR_TEXTURE, x, y, 0, 5, drawnWidth, BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }

                long time = client.level.getGameTime();
                float sineWave = (Mth.sin((time + tickDelta) * 0.15f) + 1f) / 2f; 
                float alpha = 0.1f + (sineWave * 0.5f); 

                RenderSystem.enableBlend();
                RenderSystem.blendFunc(
                    com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, 
                    com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE
                );
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha); 

                if (drawnWidth > 0) {
                    guiGraphics.blit(REIATSU_BAR_TEXTURE, x, y, 0, 5, drawnWidth, BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                }

                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
        });
    }
}
