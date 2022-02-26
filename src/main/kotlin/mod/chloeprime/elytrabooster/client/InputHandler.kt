package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isFlyingWithBooster
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.network.CElytraInputPacket
import mod.chloeprime.elytrabooster.common.network.ModNetworking
import mod.chloeprime.elytrabooster.common.util.Aerodynamics
import mod.chloeprime.elytrabooster.common.util.Time
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
internal object InputHandler {
    private var input: IElytraInputCap? = null
    private val BOOST_FORCE_ATTRIBUTE by lazy {
        ElytraBoosterApi.Attributes.BOOST_SPEED.get()
    }

    @SubscribeEvent
    fun onInput(e: TickEvent.ClientTickEvent) {
        // 如果玩家没有进入世界，则返回
        if (Minecraft.getInstance().world == null) return
        val player = Minecraft.getInstance().player ?: return
        if (!player.isFlyingWithBooster) return

        recordInput(player)
    }

    /**
     * 处理转向
     */
    @SubscribeEvent
    fun handleTurning(e: TickEvent.RenderTickEvent) {
        if (input == null) return
        if (input!!.moveStrafe in -1e-5..1e-5 && input!!.moveForward in -1e-5..1e-5) {
            return
        }

        val player = Minecraft.getInstance().player ?: return
        if (!player.isFlyingWithBooster) return

        val maxSpeed = player.getAttributeValue(BOOST_FORCE_ATTRIBUTE)
        player.rotationYaw +=
            Aerodynamics.getAngularAcceleration(player.motion, maxSpeed.toFloat()) *
                -input!!.moveStrafe * CameraRoll.rollRate * Time.deltaTime
    }

    private fun recordInput(player: ClientPlayerEntity) {
        input = ElytraBoosterApi.getElytraInputOrNull(player) ?: return

        val curInput = player.movementInput
        val dirty =
            input!!.moveStrafe != curInput.moveStrafe ||
                    input!!.moveForward != curInput.moveForward

        input!!.moveStrafe = curInput.moveStrafe
        input!!.moveForward = curInput.moveForward

        if (dirty) {
            ModNetworking.CHANNEL.sendToServer(CElytraInputPacket(input!!))
        }
    }
}
