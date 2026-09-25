package dev.lakel.bleach.item; 

import dev.lakel.bleach.BleachMod; 
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsauchiItem extends SwordItem implements GeoItem {

    public static Supplier<Object> CLIENT_RENDER_PROVIDER = null;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public AsauchiItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        if (CLIENT_RENDER_PROVIDER != null) {
            return CLIENT_RENDER_PROVIDER;
        }
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            boolean hasReiatsu = player.getTags().contains("substitute_shinigami") || player.getTags().contains("true_shinigami");
            if (hasReiatsu) {
                CompoundTag nbt = stack.getOrCreateTag();
                if (!nbt.contains("OwnerUUID")) {
                    nbt.putUUID("OwnerUUID", player.getUUID());
                    nbt.putString("OwnerName", player.getName().getString());
                    nbt.putInt("SpiritualXP", 0);
                    player.displayClientMessage(Component.literal("§7L'Asauchi a scellé une connexion avec votre âme..."), true);
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            boolean hasReiatsu = player.getTags().contains("substitute_shinigami") || player.getTags().contains("true_shinigami");
            CompoundTag nbt = stack.getOrCreateTag();
            
            if (hasReiatsu && nbt.contains("OwnerUUID") && nbt.getUUID("OwnerUUID").equals(player.getUUID())) {
                if (target.getType().is(BleachMod.HOLLOWS_TAG)) {
                    if (target.getHealth() <= 0.0f || target.isDeadOrDying()) {
                        int xp = nbt.getInt("SpiritualXP");
                        nbt.putInt("SpiritualXP", xp + 1);
                        
                        if (xp + 1 == 50) {
                            player.displayClientMessage(Component.literal("§bVotre Zanpakutō vibre intensément... Il est prêt à s'éveiller."), false);
                        }
                    }
                }
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("OwnerName")) {
            tooltip.add(Component.literal("§7Propriétaire : §f" + nbt.getString("OwnerName")));
            tooltip.add(Component.literal("§bÂmes purifiées : §f" + nbt.getInt("SpiritualXP")));
        } else {
            tooltip.add(Component.literal("§8Lame vierge... (Nécessite du Reiatsu pour être liée)"));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
