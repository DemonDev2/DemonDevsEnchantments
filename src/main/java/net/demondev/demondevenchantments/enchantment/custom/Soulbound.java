package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = DemonDevEnchantments.MOD_ID)
public class Soulbound extends Enchantment {

    private static final List<ItemStack> soulboundItems = new ArrayList<>();

    public Soulbound() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.VANISHING_CURSE;
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        soulboundItems.clear();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack itemStack = player.getInventory().items.get(i);
            if (itemStack.isEnchanted() && itemStack.getEnchantmentLevel(ModEnchantments.SOULBOUND.get()) > 0) {
                soulboundItems.add(itemStack.copy());
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!soulboundItems.isEmpty()) {
            for (ItemStack soulboundItem : soulboundItems) {
                player.getInventory().add(soulboundItem);
            }
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));  // 10 seconds of regeneration
        }
        soulboundItems.clear();
    }
}
