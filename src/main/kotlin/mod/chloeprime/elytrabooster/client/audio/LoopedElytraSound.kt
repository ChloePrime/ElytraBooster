package mod.chloeprime.elytrabooster.client.audio

import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth.*
import net.minecraft.world.entity.LivingEntity
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
) : ElytraSoundBase(entity, se, SoundSource.PLAYERS) {

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
        looping = true
        delay = 0
        volume = if (fadeIn == 0f) 1f else 0f
        updatePos()
    }
}