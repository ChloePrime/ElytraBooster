package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.config.LazyFormula
import mod.chloeprime.elytrabooster.common.config.wrap
import net.minecraft.item.Item
import net.minecraft.item.Rarity
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModItems {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ElytraBoosterMod.MODID)!!

    /**
     * 普通的电推鞘翅
     */
    val BOOSTED_ELYTRA_FE_T1 = REGISTRY.register("boosted_elytra_fe_t1") {
        BoostedElytraItem(
            BoostedElytraProperties().apply {
                boostForce { ElyBoosterModConfig.FE_T1_BOOST_FORCE.get() }
                maxEnergy { ElyBoosterModConfig.FE_T1_MAX_FE.get() }
                costFormula = LazyFormula { ElyBoosterModConfig.FE_T1_FE_COST.get() }.wrap()
                maxDamage(ElyBoosterModConfig.FE_T1_DURABILITY.get())
            }
        )
    }!!

    /**
     * 高级电推鞘翅
     */
    val BOOSTED_ELYTRA_FE_T2 = REGISTRY.register("boosted_elytra_fe_t2") {
        BoostedElytraItem(
            BoostedElytraProperties().apply {
                boostForce { ElyBoosterModConfig.FE_T2_BOOST_FORCE.get() }
                maxEnergy { ElyBoosterModConfig.FE_T2_MAX_FE.get() }
                costFormula = LazyFormula { ElyBoosterModConfig.FE_T2_FE_COST.get() }.wrap()
                maxDamage(ElyBoosterModConfig.FE_T2_DURABILITY.get())
                rarity(Rarity.UNCOMMON)
            }
        )
    }!!

    val CREATIVE_BOOSTED_ELYTRA = REGISTRY.register("boosted_elytra_creative") {
        @Suppress("MoveLambdaOutsideParentheses")
        BoostedElytraItemBase(
            Item.Properties().maxStackSize(1).rarity(Rarity.EPIC),
            { ElyBoosterModConfig.CREATIVE_BOOST_POWER.get() },
        )
    }!!

    /* 合成材料 */

    val JET_FUEL = REGISTRY.register("jet_fuel") {
        Item(Item.Properties().group(ModItemGroup))
    }!!

    val PLASMA_THRUSTER = REGISTRY.register("plasma_thruster") {
        Item(Item.Properties().group(ModItemGroup))
    }!!
}
