package mod.chloeprime.elytrabooster

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.common.block.ModBlocks
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.fluid.ModFluids
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

/**
 * TODO 来点免撞击伤害和加强动力撞碎方块
 * @author ChloePrime
 */
@Mod(ElytraBoosterMod.MODID)
object ElytraBoosterMod {
    const val MODID = "elytra_booster"
    val LOGGER = LogManager.getLogger("ElytraBooster Logger")

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ElyBoosterModConfig.CONFIG)

        val eventBus = MOD_CONTEXT.getKEventBus()
        ModBlocks.REGISTRY.register(eventBus)
        ModItems.REGISTRY.register(eventBus)
        ModFluids.REGISTRY.enqueueToBus(eventBus)
        ElytraBoosterApi.Attributes.REGISTRY.register(MOD_CONTEXT.getKEventBus())
    }
}
