package dev.lakel.bleach.item; 

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.List;

public class ShinigamiBadgeItem extends Item {

    private static final TagKey<EntityType<?>> HOLLOWS_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("bleach_mod", "hollows"));

    public ShinigamiBadgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            
            List<LivingEntity> hollows = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(30.0), e -> e.getType().is(HOLLOWS_TAG));
            
            if (!hollows.isEmpty()) {
                double minDistance = 30.0;
                for (LivingEntity hollow : hollows) {
                    double dist = player.distanceTo(hollow);
                    if (dist < minDistance) {
                        minDistance = dist;
                    }
                }

                int tickInterval;
                if (minDistance <= 10.0) {
                    tickInterval = 10; 
                } else if (minDistance <= 20.0) {
                    tickInterval = 20; 
                } else {
                    tickInterval = 40; 
                }

                if (level.getGameTime() % tickInterval == 0) {
                    float pitch = 2.0f - (float) (minDistance / 30.0);
                    level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.0F, pitch);
                    
                    if (level instanceof ServerLevel serverLevel) {
                        for (LivingEntity hollow : hollows) {
                            DustParticleOptions redDot = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);
                            
                            serverLevel.sendParticles(
                                    redDot,
                                    hollow.getX(),
                                    hollow.getY() + hollow.getBbHeight() + 0.5,
                                    hollow.getZ(),
                                    3,
                                    0.1, 0.1, 0.1,
                                    0.0
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            
            boolean isSubstitute = player.getTags().contains("substitute_shinigami");

            if (isSubstitute) {
                String revokeCommand = "power revoke " + serverPlayer.getScoreboardName() + " bleach_mod:reiatsu_resource";
                level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), revokeCommand);
                
                player.removeTag("substitute_shinigami");
                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.literal("§aVous avez réintégré votre corps."), true);

            } else {
                String grantCommand = "power grant " + serverPlayer.getScoreboardName() + " bleach_mod:reiatsu_resource";
                int success = level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), grantCommand);

                if (success > 0) {
                    player.addTag("substitute_shinigami");
                    
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false));
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2, false, false));
                    level.playSound(null, player.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.literal("§cVous êtes séparé de votre corps !"), true);
                    
                } else {
                    player.displayClientMessage(Component.literal("§eLe Daikōshō résonne avec votre pression spirituelle..."), true);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
