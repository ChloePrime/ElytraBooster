package mod.chloeprime.elytrabooster.common

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import net.minecraftforge.event.entity.EntityAttributeModificationEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/**
 * 解决Could not find attribute的问题
 *
 * @author ChloePrime
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object AttributeRegistering {
    @SubscribeEvent
    fun setupAttributes(e: EntityAttributeModificationEvent) {
        e.types.forEach {
            e.add(it, ElytraBoosterApi.Attributes.BOOST_FORCE.get())
        }
    }
}
