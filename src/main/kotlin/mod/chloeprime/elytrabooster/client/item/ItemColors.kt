package mod.chloeprime.elytrabooster.client.item

import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemColors {
    private const val JET_FUEL_COLOR = 0x9EF94A

    @SubscribeEvent
    fun onItemColor(e: ColorHandlerEvent.Item) {
        e.itemColors.register({ _, layerIdx ->
            return@register if (layerIdx > 0) -1 else JET_FUEL_COLOR
        }, ModItems.JET_FUEL.get())
    }
}
