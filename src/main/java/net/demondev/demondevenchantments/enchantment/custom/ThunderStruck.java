package net.demondev.demondevenchantments.enchantment.custom;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = DemonDevEnchantments.MOD_ID)
public class ThunderStruck extends Enchantment {
    public ThunderStruck() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.FLAMING_ARROWS;
    }


    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getProjectile();
        Level world = projectile.level(); // Using level() method instead of level field

        // Ensure we are only working with arrows
        if (projectile instanceof AbstractArrow arrow && !world.isClientSide()) {
            if (arrow.getOwner() instanceof Player player) {
                ItemStack bow = player.getMainHandItem();

                // Check if the bow has the Thunderstruck enchantment
                if (bow.getEnchantmentLevel(ModEnchantments.THUNDERSTRUCK.get()) > 0) {
                    // Check if the hit result type is ENTITY
                    if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                        // Cast to EntityHitResult to access the entity
                        if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult) {
                            Entity hitEntity = entityHitResult.getEntity();

                            // Summon lightning at the entity's position
                            if (world instanceof ServerLevel serverWorld) {
                                Vec3 hitPos = hitEntity.position(); // Get the position of the entity that was hit
                                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverWorld);
                                assert lightningBolt != null;
                                lightningBolt.moveTo(hitPos.x, hitPos.y, hitPos.z); // Position the lightning at the hit entity
                                serverWorld.addFreshEntity(lightningBolt); // Add the lightning entity to the world
                            }
                        }
                    }
                }
            }
        }
     }
  }


