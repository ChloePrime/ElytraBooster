package mod.chloeprime.elytrabooster.client.audio

import net.minecraft.client.audio.ISound
import net.minecraft.entity.LivingEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class LoopedElytraSoundStart(
    entity: LivingEntity,
    se: SoundEvent,
    private val loopSound: (LivingEntity) -> ISound,
    private val loopStart: Float
) : ElytraSoundBase(entity, se, SoundCategory.PLAYERS) {

    override fun tick() {
        super.tick()

        if (bLoopPlayed) {
            return
        }
        val passed = (System.nanoTime() - startTimeNanos) * 1e-9f
        if (passed < loopStart - ONE_TICK) {
            return
        }
        val loopBody = loopSound.invoke(entity)
        (loopBody as? ElytraSoundBase)?.scaleVolume(volumeScale)
        playSoundLater(loopBody)

        bLoopPlayed = true
    }

    override fun scaleVolume(newScale: Float) {
        super.scaleVolume(newScale)
        volume = volumeScale
    }

    init {
        repeat = false
        volume = volumeScale
    }

    private var bLoopPlayed = false

    companion object {
        private const val ONE_TICK = 0.05f
    }
}