package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ForgottenFortune extends Enchantment {

    private static final double DROP_CHANCE = 0.05;
    private static final Random RANDOM = new Random();

    public ForgottenFortune() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(new LootEventHandler());
    }
    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.BLOCK_FORTUNE && ench != Enchantments.SILK_TOUCH;
    }

    @Mod.EventBusSubscriber(modid = DemonDevEnchantments.MOD_ID)
    public static class LootEventHandler {

        @SubscribeEvent
        public void onBlockBreak(BlockEvent.BreakEvent event) {
            Player player = event.getPlayer();
            if (player == null || player.level().isClientSide()) {
                return;
            }
            ItemStack tool = player.getItemBySlot(EquipmentSlot.MAINHAND);
            int enchantmentLevel = tool.getEnchantmentLevel(ModEnchantments.FORGOTTEN_FORTUNE.get());
            if (enchantmentLevel > 0 && isTargetOre(event.getState().getBlock()) && RANDOM.nextDouble() < DROP_CHANCE) {
                triggerLootDrop(player, event.getPos(), (ServerLevel) player.level(), event.getState());
            }
        }

        private boolean isTargetOre(Block block) {
            return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE ||
                    block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE;
        }

        private static void triggerLootDrop(Player player, BlockPos pos, ServerLevel world, BlockState state) {
            ResourceLocation lootTableLocation = RANDOM.nextBoolean()
                    ? BuiltInLootTables.END_CITY_TREASURE
                    : BuiltInLootTables.ANCIENT_CITY;
            LootTable lootTable = world.getServer().getLootData().getLootTable(lootTableLocation);
            LootParams.Builder paramsBuilder = new LootParams.Builder(world)
                    .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                    .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                    .withParameter(LootContextParams.BLOCK_STATE, state)
                    .withParameter(LootContextParams.THIS_ENTITY, player);
            lootTable.getRandomItems(paramsBuilder.create(LootContextParamSets.BLOCK), stack -> Block.popResource(world, pos, stack));
        }
    }
}
