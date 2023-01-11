package mod.chloeprime.elytrabooster.common.util

import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import net.minecraft.world.item.FireworkRocketItem
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/**
 * 禁止原版烟花加速鞘翅。
 * 可在配置文件中修改。
 */
@Mod.EventBusSubscriber
object FireworksBoostBanning {
    @SubscribeEvent
    fun onRightClick(e: PlayerInteractEvent.RightClickItem) {
        if (!ElyBoosterModConfig.BAN_FIREWORK_BOOST.get()) return
        if (e.itemStack.item !is FireworkRocketItem) return
        e.isCanceled = true
    }
}
