package mod.chloeprime.elytrabooster.client.audio

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isBoosting
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
abstract class ElytraSoundBase(
    protected val entity: LivingEntity,
    se: SoundEvent,
    category: SoundSource,
) : AbstractTickableSoundInstance(se, category) {

    /**
     * 在 tick() 中使用 ISoundHandler.play 将会导致 CME，
     * 所以需要延迟 1tick 播放音效的循环部分。
     */
    protected fun playSoundLater(sound: SoundInstance) {
        Minecraft.getInstance().tell {
            Minecraft.getInstance().soundManager.play(sound)
        }
    }

    protected var volumeScale = 1f
        private set

    open fun scaleVolume(newScale: Float) {
        volumeScale = newScale
    }

    override fun canStartSilent() = true
    override fun canPlaySound(): Boolean {
        return entity.isAlive && entity.isBoosting
    }

    override fun tick() {
        updatePos()
    }

    protected fun updatePos() {
        x = entity.x
        y = entity.y
        z = entity.z
    }

    protected val startTimeNanos = System.nanoTime()
}