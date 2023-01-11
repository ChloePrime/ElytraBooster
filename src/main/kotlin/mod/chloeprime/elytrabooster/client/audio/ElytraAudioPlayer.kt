package mod.chloeprime.elytrabooster.client.audio

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isBoosting
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
object ElytraAudioPlayer {
    private var bPlaying = false

    private fun play(p: LocalPlayer) {
        val snd = ElytraAudioRegistry.new(p) ?: return
        Minecraft.getInstance().soundManager.play(snd)
    }

    @SubscribeEvent
    fun onClientTick(e: TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.END) return

        val player = Minecraft.getInstance().player ?: return
        val shouldPlay = player.isBoosting
        if (shouldPlay && !bPlaying) {
            play(player)
            bPlaying = true
        } else if (!shouldPlay && bPlaying) {
            bPlaying = false
        }
    }

//    @SubscribeEvent
//    fun onElytraInput(e: BoostedElytraInputEvent) {
//        if (e.input.isBoosting && e.forwardChanged) {
//            bPlaying = true
//            play()
//        }
//    }
}