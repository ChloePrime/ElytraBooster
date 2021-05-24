package mod.chloeprime.elytrabooster.client.fluid

import mod.chloeprime.elytrabooster.common.fluid.ModFluids
import mod.chloeprime.elytrabooster.common.fluid.util.FluidRegistryEntry
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
object FluidRenderTypeRegistry {
    @SubscribeEvent
    fun onClientSetup(e: FMLClientSetupEvent) {
        e.enqueueWork {
            ModFluids.REGISTRY.entries.forEach(::registerEntry)
        }
    }

    private fun registerEntry(entry: FluidRegistryEntry) {
        RenderTypeLookup.setRenderLayer(entry.source.get(), RenderType.getTranslucent())
        RenderTypeLookup.setRenderLayer(entry.flowing.get(), RenderType.getTranslucent())
    }
}
