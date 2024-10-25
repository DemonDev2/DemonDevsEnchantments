package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class LetThereBeLight extends Enchantment {

    public LetThereBeLight() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
        MinecraftForge.EVENT_BUS.register(this); // Register event listener
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.HEAD;
    }

    // Event handler to give Night Vision while helmet is equipped
    @Mod.EventBusSubscriber(modid = "demondev_enchantments", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class LetThereBeLightHandler {

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;

            // Check if the player has the Let There Be Light enchantment on their helmet
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet.isEnchanted() && helmet.getEnchantmentLevel(ModEnchantments.LET_THERE_BE_LIGHT.get()) > 0) {

                // Check if the player already has Night Vision, and reapply it if necessary
                MobEffectInstance nightVision = player.getEffect(MobEffects.NIGHT_VISION);
                if (nightVision == null || nightVision.getDuration() <= 200) {  // Reapply if it's missing or about to expire
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
                }
            } else {
                // Remove the Night Vision if the helmet is removed or enchantment is missing
                if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }
        }
    }
}
