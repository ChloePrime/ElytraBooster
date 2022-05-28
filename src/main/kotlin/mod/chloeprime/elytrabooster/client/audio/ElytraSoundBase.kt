package mod.chloeprime.elytrabooster.client.audio

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isBoosting
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.TickableSound
import net.minecraft.entity.LivingEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
abstract class ElytraSoundBase(
    protected val entity: LivingEntity,
    se: SoundEvent,
    category: SoundCategory,
) : TickableSound(se, category) {

    /**
     * 在 tick() 中使用 ISoundHandler.play 将会导致 CME，
     * 所以需要延迟 1tick 播放音效的循环部分。
     */
    protected fun playSoundLater(sound: ISound) {
        Minecraft.getInstance().enqueue {
            Minecraft.getInstance().soundHandler.play(sound)
        }
    }

    protected var volumeScale = 1f
        private set

    open fun scaleVolume(newScale: Float) {
        volumeScale = newScale
    }

    override fun canBeSilent() = true
    override fun shouldPlaySound(): Boolean {
        return entity.isAlive && entity.isBoosting
    }

    override fun tick() {
        updatePos()
    }

    protected fun updatePos() {
        x = entity.posX
        y = entity.posY
        z = entity.posZ
    }

    protected val startTimeNanos = System.nanoTime()
}