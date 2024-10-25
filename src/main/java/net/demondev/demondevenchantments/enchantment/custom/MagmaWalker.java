package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.block.ModBlocks;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

public class MagmaWalker extends Enchantment {

    // Store the positions of magma blocks with cooldowns
    private static final Map<BlockPos, Integer> magmaCooldowns = new HashMap<>();

    public MagmaWalker() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
        MinecraftForge.EVENT_BUS.register(this); // Register event listener
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.FEET;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlot.FEET;
    }

    // Event handler to turn lava into magma when walking over it
    @Mod.EventBusSubscriber(modid = "demondev_enchantments", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MagmaWalkerHandler {

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            Level world = player.level();

            // Check if the player has boots with the Magma Walker enchantment
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            int level = boots.getEnchantmentLevel(ModEnchantments.MAGMA_WALKER.get());
            if (boots.isEnchanted() && level > 0) {

                // Get the position of the block the player is walking on
                BlockPos playerPos = player.blockPosition();

                // Replace lava in an area around the player depending on enchantment level
                int radius = level + 1;  // radius 2 for level 1, radius 3 for level 2, etc.
                for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -1, -radius), playerPos.offset(radius, -1, radius))) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getFluidState().getType() == Fluids.LAVA) {
                        // Replace lava with magma and start cooldown
                        world.setBlockAndUpdate(pos, ModBlocks.CUSTOM_MAGMA_BLOCK.get().defaultBlockState());
                        magmaCooldowns.put(pos.immutable(), 200); // 10 seconds cooldown (200 ticks)
                    }
                }
            }

            // Handle block reversion cooldown
            handleMagmaCooldowns(world);
        }

        // Method to handle magma block reversion to lava after cooldown
        private static void handleMagmaCooldowns(Level world) {
            // Use a temporary map to avoid concurrent modification exceptions
            Map<BlockPos, Integer> tempMap = new HashMap<>(magmaCooldowns);

            for (Map.Entry<BlockPos, Integer> entry : tempMap.entrySet()) {
                BlockPos pos = entry.getKey();
                int timeLeft = entry.getValue();

                if (timeLeft <= 0) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() == ModBlocks.CUSTOM_MAGMA_BLOCK.get()) {
                        // Revert the magma block back to lava
                        world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                        magmaCooldowns.remove(pos); // Remove from cooldown list
                    }
                } else {
                    // Decrease the cooldown timer
                    magmaCooldowns.put(pos, timeLeft - 1);
                }
            }
        }
    }
}
