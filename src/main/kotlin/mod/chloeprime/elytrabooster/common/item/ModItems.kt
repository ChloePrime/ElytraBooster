package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.fluid.ModFluids
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

@Suppress("unused")
object ModItems {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ElytraBoosterMod.MODID)!!

    /**
     * 燃油鞘翅T1
     */
    val BOOSTED_ELYTRA_FUEL_T1 = REGISTRY.register("boosted_elytra_fuel_t1") {
        FuelBoostedElytraItem(
            ModArmorMaterials.FUEL_ARMORS,
            FuelBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FUEL_T1)
                stacksTo(1)
                fuelType = ModFluids.JET_FUEL.source
            }
        )
    }!!

    /**
     * 燃油鞘翅T2
     */
    val BOOSTED_ELYTRA_FUEL_T2 = REGISTRY.register("boosted_elytra_fuel_t2") {
        FuelBoostedElytraItem(
            ModArmorMaterials.FUEL_ARMORS,
            FuelBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FUEL_T2)
                stacksTo(1)
                fuelType = ModFluids.ROCKET_FUEL.source
            }
        )
    }!!

    /**
     * 普通的电推鞘翅
     */
    val BOOSTED_ELYTRA_FE_T1 = REGISTRY.register("boosted_elytra_fe_t1") {
        EnergyBoostedElytraItem(
            ModArmorMaterials.ELECTRIC_1,
            EnergyBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FE_T1)
                stacksTo(1)
                rarity(Rarity.UNCOMMON)
            }
        )
    }!!

    /**
     * 高级电推鞘翅
     */
    val BOOSTED_ELYTRA_FE_T2 = REGISTRY.register("boosted_elytra_fe_t2") {
        EnergyBoostedElytraItem(
            ModArmorMaterials.ELECTRIC_2,
            EnergyBoostedElytraItem.Properties().apply {
                acceptConfig(ElyBoosterModConfig.FE_T2)
                stacksTo(1)
                rarity(Rarity.RARE)
            }
        )
    }!!

    val CREATIVE_BOOSTED_ELYTRA = REGISTRY.register("boosted_elytra_creative") {
        @Suppress("MoveLambdaOutsideParentheses")
        BoostedElytraItemBase(
            ModArmorMaterials.CREATIVE_ELYTRA,
            Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
            { ElyBoosterModConfig.CREATIVE_BOOST_POWER.get() },
        )
    }!!

    /* 合成材料 */

    val JET_FUEL = REGISTRY.register("jet_fuel") {
        LiquidBottle(Item.Properties().tab(ModCreativeTab), ModFluids.JET_FUEL.source)
    }!!

    val ROCKET_FUEL = REGISTRY.register("rocket_fuel") {
        LiquidBottle(
            Item.Properties().rarity(Rarity.UNCOMMON).tab(ModCreativeTab),
            ModFluids.ROCKET_FUEL.source
        )
    }!!

    /* 燃油鞘翅合成材料 */

    val FUEL_THRUSTER = REGISTRY.register("fuel_thruster") {
        Item(Item.Properties().tab(ModCreativeTab))
    }!!

    val ROCKET_THRUSTER = REGISTRY.register("rocket_thruster") {
        Item(Item.Properties().tab(ModCreativeTab))
    }!!

    /* 电鞘翅合成材料 */

    val JET_ENGINE = REGISTRY.register("jet_engine") {
        Item(Item.Properties().tab(ModCreativeTab))
    }!!
    
    val ELECTRIC_THRUSTER = REGISTRY.register("electric_thruster") {
        Item(Item.Properties().tab(ModCreativeTab))
    }!!

    val PLASMA_THRUSTER = REGISTRY.register("plasma_thruster") {
        Item(Item.Properties().rarity(Rarity.RARE).tab(ModCreativeTab))
    }!!
}
