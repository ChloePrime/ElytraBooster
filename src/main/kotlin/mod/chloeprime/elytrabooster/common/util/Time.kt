package mod.chloeprime.elytrabooster.common.util

import mod.chloeprime.elytrabooster.client.ClientTimer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.util.thread.EffectiveSide

@Mod.EventBusSubscriber
object Time {
    val deltaTime
        get() = if (EffectiveSide.get() == LogicalSide.CLIENT) {
            getClientTime()
        } else {
            1 / 20F
        }

    @OnlyIn(Dist.CLIENT)
    private fun getClientTime() = ClientTimer.deltaTime
}
