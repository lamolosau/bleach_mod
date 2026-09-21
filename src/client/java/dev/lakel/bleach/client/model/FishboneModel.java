package dev.lakel.bleach.client.model;

import dev.lakel.bleach.entity.FishboneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FishboneModel extends GeoModel<FishboneEntity> {
    @Override
    public ResourceLocation getModelResource(FishboneEntity object) {
        return new ResourceLocation("bleach_mod", "geo/fishbone.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FishboneEntity object) {
        return new ResourceLocation("bleach_mod", "textures/entity/fishbone.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FishboneEntity object) {
        return new ResourceLocation("bleach_mod", "animations/fishbone.animation.json");
    }

    @Override
    public void setCustomAnimations(FishboneEntity animatable, long instanceId, AnimationState<FishboneEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
