package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModEnchantments {
    val REGISTRY = DeferredRegister.create(
        ForgeRegistries.ENCHANTMENTS, ElytraBoosterMod.MODID
    )!!

    val IMPACT_PROTECTION = REGISTRY.register("impact_protection") {
        ImpactProtectionEnchantment()
    }
}