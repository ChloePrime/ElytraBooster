package mod.chloeprime.elytrabooster.client.item

import mod.chloeprime.elytrabooster.common.item.IColoredItem
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/**
 * @author ChloePrime
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemColors {

    /**
     * 自动为本mod内实现了 [IColoredItem] 的物品注册颜色。
     */
    @SubscribeEvent
    fun onItemColor(e: ColorHandlerEvent.Item) {
        ModItems.REGISTRY.entries.stream()
            .map { it.get() }
            .filter { it is IColoredItem }
            .forEach {
                val color = (it as IColoredItem).color
                e.itemColors.register({ _, layerIdx ->
                    return@register if (layerIdx > 0) -1 else color
                }, it)
            }
    }
}
