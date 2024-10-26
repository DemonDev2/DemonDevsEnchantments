package net.demondev.demondevenchantments.block.custom;

import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CustomMagmaBlock extends MagmaBlock {
    public CustomMagmaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.isSteppingCarefully() && !hasMagmaWalker(livingEntity)) {
                entity.hurt(level.damageSources().hotFloor(), 1.0F);
            }
        }
    }

    private boolean hasMagmaWalker(LivingEntity entity) {
        return entity.getArmorSlots().iterator().hasNext() &&
                EnchantmentHelper.getEnchantmentLevel(ModEnchantments.MAGMA_WALKER.get(), entity) > 0;
    }
}
