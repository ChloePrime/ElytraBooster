package mod.chloeprime.elytrabooster.client.audio

import net.minecraft.entity.LivingEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.MathHelper.clamp
import net.minecraft.util.math.MathHelper.lerp
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class LoopedElytraSound(
    entity: LivingEntity,
    se: SoundEvent,
    /**
     * 单位：秒
     */
    private val fadeIn: Float = 0f,
) : ElytraSoundBase(entity, se, SoundCategory.PLAYERS) {

    override fun tick() {
        super.tick()

        if (fadeIn == 0f) {
            volume = volumeScale
            return
        }
        val passed = (System.nanoTime() - startTimeNanos) * 1e-9f
        volume = volumeScale * clamp(lerp(passed / fadeIn, 0f, 1f), 0f, 1f)
    }

    init {
        repeat = true
        repeatDelay = 0
        volume = if (fadeIn == 0f) 1f else 0f
        updatePos()
    }
}