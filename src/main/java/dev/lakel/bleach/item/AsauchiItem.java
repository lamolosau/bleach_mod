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

import java.util.List;

public class AsauchiItem extends SwordItem {

    public AsauchiItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            CompoundTag nbt = stack.getOrCreateTag();
            
            if (!nbt.contains("OwnerUUID")) {
                nbt.putUUID("OwnerUUID", player.getUUID());
                nbt.putString("OwnerName", player.getName().getString());
                nbt.putInt("SpiritualXP", 0);
                
                player.displayClientMessage(Component.literal("§7L'Asauchi a scellé une connexion avec votre âme..."), true);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            CompoundTag nbt = stack.getOrCreateTag();
            
            if (nbt.contains("OwnerUUID") && nbt.getUUID("OwnerUUID").equals(player.getUUID())) {
                
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
            tooltip.add(Component.literal("§8Lame vierge... (Prenez-la en main pour la lier)"));
        }
        
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
