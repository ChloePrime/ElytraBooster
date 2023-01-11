package mod.chloeprime.elytrabooster.client

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.abs

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
internal object ClientTimer {
    var deltaTime: Float = 0.0f
        private set

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        if (e.phase == TickEvent.Phase.END) return
        refresh()
    }

    private var lastIntTick = 0L
    private var lastPartial = 0f

    private var cachedPartial = 0f

    private fun refresh() {
        val partial = Minecraft.getInstance().frameTime
        //防止重复调用多次刷新
        if (abs(partial - cachedPartial) <= Float.MIN_VALUE) {
            return
        }
        cachedPartial = partial

        val tickBefore = lastIntTick
        val partialBefore = lastPartial
        refreshStartTime()
        deltaTime = (lastIntTick - tickBefore) + (lastPartial - partialBefore)
    }
    private fun refreshStartTime() {
        lastIntTick = Minecraft.getInstance().level?.gameTime ?: return
        lastPartial = Minecraft.getInstance().frameTime
    }
}