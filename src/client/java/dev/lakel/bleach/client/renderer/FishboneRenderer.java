package dev.lakel.bleach.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lakel.bleach.client.model.FishboneModel;
import dev.lakel.bleach.entity.FishboneEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FishboneRenderer extends GeoEntityRenderer<FishboneEntity> {
    
    // Identifiant du pouvoir permettant de voir les esprits
    private static final ResourceLocation REIATSU_POWER_ID = new ResourceLocation("bleach_mod", "reiatsu_resource");

    public FishboneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FishboneModel());
    }

    @Override
    public void render(FishboneEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Minecraft client = Minecraft.getInstance();
        
        if (client.player != null) {
            boolean canSeeSpirits = false;
            
            // Vérification de la possession du pouvoir par le joueur local
            if (PowerTypeRegistry.contains(REIATSU_POWER_ID)) {
                PowerType<?> powerType = PowerTypeRegistry.get(REIATSU_POWER_ID);
                if (PowerHolderComponent.KEY.get(client.player).hasPower(powerType)) {
                    canSeeSpirits = true;
                }
            }

            // Si le joueur n'a pas le Reiatsu (ex: humain normal), on annule le rendu 3D
            if (!canSeeSpirits) {
                return; 
            }
        }

        // Si le joueur a le pouvoir, l'entité est dessinée normalement via GeckoLib
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
