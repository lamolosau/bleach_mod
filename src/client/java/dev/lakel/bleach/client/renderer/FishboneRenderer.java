package dev.lakel.bleach.client.renderer;

import dev.lakel.bleach.client.model.FishboneModel;
import dev.lakel.bleach.entity.FishboneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FishboneRenderer extends GeoEntityRenderer<FishboneEntity> {
    public FishboneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FishboneModel());
    }
}
