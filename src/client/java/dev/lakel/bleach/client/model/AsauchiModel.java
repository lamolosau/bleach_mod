package dev.lakel.bleach.client.model;

import dev.lakel.bleach.item.AsauchiItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AsauchiModel extends GeoModel<AsauchiItem> {

    @Override
    public ResourceLocation getModelResource(AsauchiItem animatable) {
        return new ResourceLocation("bleach_mod", "geo/item/asauchi.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AsauchiItem animatable) {
        return new ResourceLocation("bleach_mod", "textures/item/asauchi.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AsauchiItem animatable) {
        return new ResourceLocation("bleach_mod", "animations/item/asauchi.animation.json");
    }
}
