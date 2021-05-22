package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import net.minecraft.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.ToIntFunction
import kotlin.math.abs

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
                costFormula = ToIntFunction {
                    (abs(it.moveForward) * 50 + abs(it.moveStrafe) * 10).toInt() + 2
                }
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
