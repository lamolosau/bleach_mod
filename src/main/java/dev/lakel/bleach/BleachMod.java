package dev.lakel.bleach;

import dev.lakel.bleach.entity.FishboneEntity;
import dev.lakel.bleach.entity.InertBodyEntity;
import dev.lakel.bleach.item.AsauchiItem;
import dev.lakel.bleach.item.ShinigamiBadgeItem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.VariableIntPower;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BleachMod implements ModInitializer {
    public static final String MOD_ID = "bleach_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static final Map<UUID, Long> LAST_REIATSU_USE = new HashMap<>();

    public static final TagKey<EntityType<?>> HOLLOWS_TAG = TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, new ResourceLocation("bleach_mod", "hollows"));
    
    public static final EntityType<FishboneEntity> FISHBONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "fishbone"),
            FabricEntityTypeBuilder.create(MobCategory.MONSTER, FishboneEntity::new).dimensions(EntityDimensions.fixed(1.7f, 3.0f)).build()
    );

    public static final ResourceLocation FISHBONE_ROAR_ID = new ResourceLocation(MOD_ID, "entity.fishbone.roar");
    public static final SoundEvent FISHBONE_ROAR_EVENT = SoundEvent.createVariableRangeEvent(FISHBONE_ROAR_ID);
    public static final Item SHINIGAMI_BADGE = new ShinigamiBadgeItem(new Item.Properties().stacksTo(1));
    public static final Item ASAUCHI = new AsauchiItem(Tiers.IRON, 3, -2.4F, new Item.Properties());
    
    public static final EntityType<InertBodyEntity> INERT_BODY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, 
            new ResourceLocation("bleach_mod", "inert_body"), 
            FabricEntityTypeBuilder.create(MobCategory.MISC, InertBodyEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Initialisation de Bleach Mod !");

        FabricDefaultAttributeRegistry.register(FISHBONE, FishboneEntity.createAttributes());
        Registry.register(BuiltInRegistries.SOUND_EVENT, FISHBONE_ROAR_ID, FISHBONE_ROAR_EVENT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("bleach_mod", "asauchi"), ASAUCHI);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("bleach_mod", "shinigami_badge"), SHINIGAMI_BADGE);
        FabricDefaultAttributeRegistry.register(INERT_BODY, InertBodyEntity.createMobAttributes());
        
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.getTags().contains("substitute_shinigami")) {
                String revokeCommand = "power revoke " + newPlayer.getScoreboardName() + " bleach_mod:reiatsu_resource";
                newPlayer.getServer().getCommands().performPrefixedCommand(newPlayer.getServer().createCommandSourceStack(), revokeCommand);
                newPlayer.removeTag("substitute_shinigami");
                newPlayer.displayClientMessage(net.minecraft.network.chat.Component.literal("§eLa mort a forcé votre âme à réintégrer votre enveloppe charnelle."), false);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long time = server.getOverworld().getGameTime();
            
            if (time % 10 == 0) {
                ResourceLocation reiatsuId = new ResourceLocation("bleach_mod", "reiatsu_resource");
                if (!PowerTypeRegistry.contains(reiatsuId)) return;
                PowerType<?> powerType = PowerTypeRegistry.get(reiatsuId);

                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (player.getTags().contains("substitute_shinigami") || player.getTags().contains("true_shinigami")) {
                        
                        long lastUse = LAST_REIATSU_USE.getOrDefault(player.getUUID(), 0L);
                        
                        if (time - lastUse >= 200) {
                            PowerHolderComponent component = PowerHolderComponent.KEY.get(player);
                            if (component.hasPower(powerType) && component.getPower(powerType) instanceof VariableIntPower resPower) {
                                int current = resPower.getValue();
                                int max = resPower.getMax();
                                
                                if (current < max) {
                                    resPower.setValue(current + 1);
                                    PowerHolderComponent.KEY.sync(player);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
