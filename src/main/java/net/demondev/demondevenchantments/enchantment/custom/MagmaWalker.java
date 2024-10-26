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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

public class MagmaWalker extends Enchantment {
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


    @Mod.EventBusSubscriber(modid = "demondev_enchantments", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MagmaWalkerHandler {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            Level world = player.level();
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            int level = boots.getEnchantmentLevel(ModEnchantments.MAGMA_WALKER.get());
            if (boots.isEnchanted() && level > 0) {
                BlockPos playerPos = player.blockPosition();
                int radius = level + 1;
                for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -1, -radius), playerPos.offset(radius, -1, radius))) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getFluidState().getType() == Fluids.LAVA) {
                        world.setBlockAndUpdate(pos, ModBlocks.CUSTOM_MAGMA_BLOCK.get().defaultBlockState());
                        magmaCooldowns.put(pos.immutable(), 200);
                    }
                }
            }
            handleMagmaCooldowns(world);
        }
        private static void handleMagmaCooldowns(Level world) {
            Map<BlockPos, Integer> tempMap = new HashMap<>(magmaCooldowns);

            for (Map.Entry<BlockPos, Integer> entry : tempMap.entrySet()) {
                BlockPos pos = entry.getKey();
                int timeLeft = entry.getValue();

                if (timeLeft <= 0) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() == ModBlocks.CUSTOM_MAGMA_BLOCK.get()) {
                        world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                        magmaCooldowns.remove(pos);
                    }
                } else {
                    magmaCooldowns.put(pos, timeLeft - 1);
                }
            }
        }
    }
}
