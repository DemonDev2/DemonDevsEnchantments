package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AutoSmelt extends Enchantment {

    private static final Map<Block, ItemStack> SMELTABLES = new HashMap<>();
    private static final Random RANDOM = new Random();
    static {
        SMELTABLES.put(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT));
        SMELTABLES.put(Blocks.DEEPSLATE_IRON_ORE, new ItemStack(Items.IRON_INGOT));
        SMELTABLES.put(Blocks.GOLD_ORE, new ItemStack(Items.GOLD_INGOT));
        SMELTABLES.put(Blocks.DEEPSLATE_GOLD_ORE, new ItemStack(Items.GOLD_INGOT));
        SMELTABLES.put(Blocks.COPPER_ORE, new ItemStack(Items.COPPER_INGOT));
        SMELTABLES.put(Blocks.DEEPSLATE_COPPER_ORE, new ItemStack(Items.COPPER_INGOT));
        SMELTABLES.put(Blocks.ANCIENT_DEBRIS, new ItemStack(Items.NETHERITE_SCRAP));
    }

    public AutoSmelt() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
            if (world.isClientSide()) return;

            if (tool.getItem() instanceof PickaxeItem && tool.getEnchantmentLevel(ModEnchantments.AUTO_SMELT.get()) > 0) {
                if (SMELTABLES.containsKey(block)) {
                    ItemStack smeltedItem = SMELTABLES.get(block);
                    if (smeltedItem == null || smeltedItem.isEmpty()) {
                        return;
                    }

                    if (block == Blocks.ANCIENT_DEBRIS) {
                        Block.popResource(world, event.getPos(), smeltedItem.copy());
                        world.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 11);
                        event.setExpToDrop(2);
                    } else {
                        int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
                        int quantity = calculateSmeltedQuantity(fortuneLevel);
                        for (int i = 0; i < quantity; i++) {
                            Block.popResource(world, event.getPos(), smeltedItem.copy());
                        }
                        world.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 11);
                        event.setExpToDrop(1 + fortuneLevel * 2);
                    }
                }
            }
        }
        private static int calculateSmeltedQuantity(int fortuneLevel) {
            int baseQuantity = 1;
            if (fortuneLevel > 0) {
                int extra = RANDOM.nextInt(fortuneLevel + 1);
                return baseQuantity + extra;
            }
            return baseQuantity;
        }
    }
}
