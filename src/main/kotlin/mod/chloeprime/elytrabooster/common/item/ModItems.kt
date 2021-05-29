package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.fluid.ModFluids
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
        EnergyBoostedElytraItem(
            EnergyBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FE_T1)
                maxStackSize(1)
                rarity(Rarity.UNCOMMON)
            }
        )
    }!!

    /**
     * 高级电推鞘翅
     */
    val BOOSTED_ELYTRA_FE_T2 = REGISTRY.register("boosted_elytra_fe_t2") {
        EnergyBoostedElytraItem(
            EnergyBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FE_T2)
                maxStackSize(1)
                rarity(Rarity.RARE)
            }
        )
    }!!

    val BOOSTED_ELYTRA_FUEL_T1 = REGISTRY.register("boosted_elytra_fuel_t1") {
        FuelBoostedElytraItem(
            FuelBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FUEL_T1)
                maxStackSize(1)
                fuelType = ModFluids.JET_FUEL.source
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
        LiquidBottle(Item.Properties().group(ModItemGroup), ModFluids.JET_FUEL.source)
    }!!

    val ROCKET_FUEL = REGISTRY.register("rocket_fuel") {
        LiquidBottle(
            Item.Properties().rarity(Rarity.UNCOMMON).group(ModItemGroup),
            ModFluids.ROCKET_FUEL.source
        )
    }!!

    val JET_ENGINE = REGISTRY.register("jet_engine") {
        Item(Item.Properties().group(ModItemGroup))
    }!!
    
    val ELECTRIC_THRUSTER = REGISTRY.register("electric_thruster") {
        Item(Item.Properties().group(ModItemGroup))
    }!!

    val PLASMA_THRUSTER = REGISTRY.register("plasma_thruster") {
        Item(Item.Properties().rarity(Rarity.RARE).group(ModItemGroup))
    }!!
}
