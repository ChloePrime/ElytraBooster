package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.config.LazyFormula
import mod.chloeprime.elytrabooster.common.config.wrap
import net.minecraft.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModItems {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ElytraBoosterMod.MODID)!!

    /**
     * 普通的鞘翅
     */
    val BOOSTED_ELYTRA = REGISTRY.register("boosted_elytra") {
        BoostedElytraItem(
            BoostedElytraProperties().apply {
                boostForce = ElyBoosterModConfig.T1_BOOST_FORCE.get()
                maxEnergy = ElyBoosterModConfig.T1_MAX_FE.get()
                costFormula = LazyFormula { ElyBoosterModConfig.T1_FE_COST.get() }.wrap()
                maxDamage(ElyBoosterModConfig.T1_DURABILITY.get())
            }
        )
    }!!

    val CREATIVE_BOOSTED_ELYTRA = REGISTRY.register("boosted_elytra_creative") {
        BoostedElytraItemBase(
            Item.Properties().maxStackSize(1),
            ElyBoosterModConfig.CREATIVE_BOOST_POWER.get()
        )
    }!!
}
