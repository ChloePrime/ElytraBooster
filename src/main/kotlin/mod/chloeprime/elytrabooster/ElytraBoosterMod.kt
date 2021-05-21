package mod.chloeprime.elytrabooster

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.common.item.BoostedElytraItemBase
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

/**
 * @author ChloePrime
 */
@Mod(ElytraBoosterMod.MODID)
object ElytraBoosterMod {
    const val MODID = "elytra_booster"

    init {
        ModItems.REGISTRY.register(MOD_CONTEXT.getKEventBus())
        ElytraBoosterApi.Attributes.REGISTRY.register(MOD_CONTEXT.getKEventBus())
    }
}
