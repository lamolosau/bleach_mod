package dev.lakel.bleach.client;

import dev.lakel.bleach.entity.InertBodyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class InertBodyRenderer extends MobRenderer<InertBodyEntity, PlayerModel<InertBodyEntity>> {

    public InertBodyRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(InertBodyEntity entity) {
        Player player = Minecraft.getInstance().level.getPlayerByUUID(entity.getOwnerUUID());
        if (player instanceof AbstractClientPlayer clientPlayer) {
            return clientPlayer.getSkinTextureLocation();
        }
        return DefaultPlayerSkin.getDefaultSkin(entity.getOwnerUUID());
    }
}
