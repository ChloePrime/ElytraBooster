package mod.chloeprime.elytrabooster.client.fluid

import mod.chloeprime.elytrabooster.common.fluid.ModFluids
import mod.chloeprime.elytrabooster.common.fluid.util.FluidRegistryEntry
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
object FluidRenderTypeRegistry {
    @SubscribeEvent
    fun onClientSetup(e: FMLClientSetupEvent) {
        e.enqueueWork {
            ModFluids.REGISTRY.entries.forEach(::registerEntry)
        }
    }

    private fun registerEntry(entry: FluidRegistryEntry) {
        ItemBlockRenderTypes.setRenderLayer(entry.source.get(), RenderType.translucent())
        ItemBlockRenderTypes.setRenderLayer(entry.flowing.get(), RenderType.translucent())
    }
}
