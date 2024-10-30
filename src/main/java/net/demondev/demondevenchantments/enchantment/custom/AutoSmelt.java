package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.Random;

public class AutoSmelt extends Enchantment {

    private static final Random RANDOM = new Random();

    public AutoSmelt() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.SILK_TOUCH;
    }

    @Mod.EventBusSubscriber(modid = DemonDevEnchantments.MOD_ID)
    public static class SmeltEventHandler {

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            ItemStack tool = event.getPlayer().getMainHandItem();
            BlockState state = event.getState();
            Level world = (Level) event.getLevel();
            Block block = state.getBlock();
            if (world.isClientSide()) {
                return;
            }
            if (block.defaultBlockState().is(Tags.Blocks.ORES)) {
                if (tool.getItem() instanceof PickaxeItem && tool.getEnchantmentLevel(ModEnchantments.AUTO_SMELT.get()) > 0) {
                    Optional<SmeltingRecipe> smeltingRecipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack(block.asItem())), world);
                    smeltingRecipe.ifPresent(recipe -> {
                        ItemStack smeltedItem = recipe.getResultItem(world.registryAccess());
                        if (!smeltedItem.isEmpty()) {
                            int quantity = calculateSmeltedQuantity(tool, block);
                            for (int i = 0; i < quantity; i++) {
                                Block.popResource(world, event.getPos(), smeltedItem.copy());
                            }
                            world.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 11);
                            event.setExpToDrop((int) recipe.getExperience());
                        }
                    });
                }
            }
        }
        private static int calculateSmeltedQuantity(ItemStack tool, Block block) {
            int baseQuantity = 1;
            if (block == Blocks.ANCIENT_DEBRIS) {
                return baseQuantity;
            }
            int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            if (fortuneLevel > 0) {
                int extra = RANDOM.nextInt(fortuneLevel + 1);
                return baseQuantity + extra;
            }
            return baseQuantity;
        }
    }
}
