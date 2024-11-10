package net.demondev.demondevenchantments.enchantment;

import net.demondev.demondevenchantments.DemonDevEnchantments;
import net.demondev.demondevenchantments.enchantment.custom.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, DemonDevEnchantments.MOD_ID);


    public static final RegistryObject<Enchantment> AUTO_SMELT = ENCHANTMENTS.register("auto_smelt",
            AutoSmelt::new);
    public static final RegistryObject<Enchantment> LET_THERE_BE_LIGHT = ENCHANTMENTS.register("let_there_be_light",
            LetThereBeLight::new);
    public static final RegistryObject<Enchantment> MAGMA_WALKER = ENCHANTMENTS.register("magma_walker",
            MagmaWalker::new);
    public static final RegistryObject<Enchantment> SOULBOUND = ENCHANTMENTS.register("soulbound",
            Soulbound::new);
    public static final RegistryObject<Enchantment> THUNDERSTRUCK = ENCHANTMENTS.register("thunderstruck",
            ThunderStruck::new);
    public static final RegistryObject<Enchantment> FORGOTTEN_FORTUNE = ENCHANTMENTS.register("forgotten_fortune",
            ForgottenFortune::new);





    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}