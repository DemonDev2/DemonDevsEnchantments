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
import org.jetbrains.annotations.NotNull;

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
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.SILK_TOUCH;
    }

    @Mod.EventBusSubscriber(modid = DemonDevEnchantments.MOD_ID)
    public static class SmeltEventHandler {

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            ItemStack tool = event.getPlayer().getMainHandItem();
            BlockState state = event.getState();
            Level world = (Level) event.getLevel();

            if (world.isClientSide()) {
                return;
            }

            if (state.is(Tags.Blocks.ORES)) {
                if (tool.getItem() instanceof PickaxeItem && tool.getEnchantmentLevel(ModEnchantments.AUTO_SMELT.get()) > 0) {
                    Optional<SmeltingRecipe> smeltingRecipe = world.getRecipeManager()
                            .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack(state.getBlock().asItem())), world);

                    smeltingRecipe.ifPresent(recipe -> {
                        ItemStack smeltedItem = recipe.getResultItem(world.registryAccess());
                        if (!smeltedItem.isEmpty()) {
                            int quantity = calculateSmeltedQuantity(tool);
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

        private static int calculateSmeltedQuantity(ItemStack tool) {
            int baseQuantity = 1;
            int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            return baseQuantity + (fortuneLevel > 0 ? RANDOM.nextInt(fortuneLevel + 1) : 0);
        }
    }
}
