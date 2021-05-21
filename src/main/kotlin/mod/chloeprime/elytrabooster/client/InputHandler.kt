package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
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

    @SubscribeEvent
    fun onInput(e: TickEvent.ClientTickEvent) {
        // 如果玩家没有进入世界，则返回
        if (Minecraft.getInstance().world == null) return
        val player = Minecraft.getInstance().player ?: return
        if (!ElytraBoosterApi.isFlyingWithBooster(player)) return

        input = recordInput(player)
    }

    /**
     * 处理转向
     */
    @SubscribeEvent
    fun handleTurning(e: TickEvent.RenderTickEvent) {
        if (input == null || input!!.moveStrafe in -1e-5..1e-5) return

        val player = Minecraft.getInstance().player ?: return
        if (!ElytraBoosterApi.isFlyingWithBooster(player)) return

        player.rotationYaw += Aerodynamics.getAngularAcceleration(player.motion) *
                -input!!.moveStrafe * Time.deltaTime
    }

    private fun recordInput(player: ClientPlayerEntity): IElytraInputCap {
        val inputStorage = ElytraBoosterApi.getElytraInput(player)

        val curInput = player.movementInput
        val dirty =
            inputStorage.moveStrafe != curInput.moveStrafe ||
                    inputStorage.moveForward != curInput.moveForward

        inputStorage.moveStrafe = curInput.moveStrafe
        inputStorage.moveForward = curInput.moveForward

        if (dirty) {
            ModNetworking.CHANNEL.sendToServer(CElytraInputPacket(inputStorage))
        }

        return inputStorage
    }
}
