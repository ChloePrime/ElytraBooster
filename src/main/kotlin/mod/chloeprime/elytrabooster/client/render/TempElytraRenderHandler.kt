package mod.chloeprime.elytrabooster.client.render

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isEquippingBoostedElytra
import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import top.theillusivec4.caelus.api.RenderElytraEvent

/**
 * 临时的鞘翅渲染处理器
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
object TempElytraRenderHandler {
    private val FUEL_T1 = ResourceLocation(
        ElytraBoosterApi.MODID,"textures/entity/elytra/fuel_t1_demo.png")
    private val FUEL_T2 = ResourceLocation(
        ElytraBoosterApi.MODID,"textures/entity/elytra/fuel_t2_demo.png")
    private val CREATIVE = ResourceLocation(
        ElytraBoosterApi.MODID,"textures/entity/elytra/creative.png")

    @SubscribeEvent
    fun onRenderElytra(e: RenderElytraEvent) {
        if (!e.entityLiving.isEquippingBoostedElytra) {
            return
        }
        val elytra = e.entityLiving.getItemBySlot(EquipmentSlotType.CHEST)
        assert(elytra.item is IBoostedElytraItem)

        val res = when(elytra.item) {
            ModItems.BOOSTED_ELYTRA_FUEL_T1.get() -> FUEL_T1
            ModItems.BOOSTED_ELYTRA_FUEL_T2.get() -> FUEL_T2
            ModItems.CREATIVE_BOOSTED_ELYTRA.get() -> CREATIVE
            else -> return
        }
        e.setRender(true)
        e.resourceLocation = res
    }
}