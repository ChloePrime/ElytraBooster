package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isFlyingWithBooster
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.client.event.BoostedElytraInputEvent
import mod.chloeprime.elytrabooster.common.enchantment.ModEnchantments
import mod.chloeprime.elytrabooster.common.network.CElytraInputPacket
import mod.chloeprime.elytrabooster.common.network.ModNetworking
import mod.chloeprime.elytrabooster.common.util.Aerodynamics
import mod.chloeprime.elytrabooster.common.util.Time
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
internal object InputHandler {
    private var input: IElytraInputCap? = null
    private val BOOST_FORCE_ATTRIBUTE by lazy {
        ElytraBoosterApi.Attributes.BOOST_FORCE.get()
    }

    @SubscribeEvent
    fun onInput(e: ClientTickEvent) {
        // 如果玩家没有进入世界，则返回
        if (e.phase == Phase.END) return
        if (Minecraft.getInstance().level == null) return
        val player = Minecraft.getInstance().player ?: return
        if (!player.isFlyingWithBooster) return

        syncInputToServer(player)
    }

    /**
     * 处理转向
     */
    @SubscribeEvent
    fun handleTurning(e: RenderTickEvent) {
        if (input == null) return
        if (input!!.moveStrafe in -1e-5..1e-5 && input!!.moveForward in -1e-5..1e-5) {
            return
        }

        val player = Minecraft.getInstance().player ?: return
        if (!player.isFlyingWithBooster) return

        val force = player.getAttributeValue(BOOST_FORCE_ATTRIBUTE)
        player.yRot +=
            Aerodynamics.getAngularAcceleration(player.deltaMovement, force.toFloat()) *
                    -input!!.moveStrafe * CameraRoll.rollRate * Time.deltaTime
    }

    private fun syncInputToServer(player: LocalPlayer) {
        val inp = ElytraBoosterApi.getElytraInputOrNull(player) ?: return
        this.input = inp

        val curInput = player.input
        val strafeDirty = inp.moveStrafe != curInput.leftImpulse
        val forwardDirty = inp.moveForward != curInput.forwardImpulse

        inp.moveStrafe = curInput.leftImpulse
        inp.moveForward = curInput.forwardImpulse

        if (strafeDirty || forwardDirty) {
            MinecraftForge.EVENT_BUS.post(BoostedElytraInputEvent(player, inp, strafeDirty, forwardDirty))
            ModNetworking.CHANNEL.sendToServer(CElytraInputPacket(inp))
        }
    }
}

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
internal object DodgeHandler {

    /**
     * 双击 A/D 移动判定为闪避的最高间隔
     */
    private const val PRESS_DELAY = 5

    private const val DODGE_SPEED_PER_LEVEL = 1.25F

    private val VEC3_UP = Vec3(0.0, 1.0, 0.0)

    private object InputCache {
        var aPressed = false
        var dPressed = false
        var aLastPressedTime = 0L
        var dLastPressedTime = 0L
        var dodgeL = false
        var dodgeR = false
    }

    @SubscribeEvent
    fun handleDodging(e: PlayerTickEvent) {
        if (e.phase == Phase.START || !e.player.level.isClientSide) {
            return
        }
        val enchantLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FLY_DODGING.get(), e.player)
        if (enchantLevel == 0) {
            return
        }
        val localPlayer = e.player as? LocalPlayer ?: return
        val now = e.player.level.gameTime
        val a = localPlayer.input.left
        val d = localPlayer.input.right

        if (a && !InputCache.aPressed && now - InputCache.aLastPressedTime <= PRESS_DELAY) {
            InputCache.dodgeL = true
        }
        if (d && !InputCache.dPressed && now - InputCache.dLastPressedTime <= PRESS_DELAY) {
            InputCache.dodgeR = true
        }

        if (a && !InputCache.aPressed) {
            InputCache.aLastPressedTime = now
        }
        if (d && !InputCache.dPressed) {
            InputCache.dLastPressedTime = now
        }

        InputCache.aPressed = a
        InputCache.dPressed = d

        if (InputCache.dodgeL) {
            doDodging(e.player, -1F * DODGE_SPEED_PER_LEVEL * enchantLevel)
            InputCache.dodgeL = false
        }
        if (InputCache.dodgeR) {
            doDodging(e.player, 1F * DODGE_SPEED_PER_LEVEL * enchantLevel)
            InputCache.dodgeR = false
        }
    }

    private fun doDodging(player: Player, xMove: Float) {
        val look = player.lookAngle
        val right = look.cross(VEC3_UP)

        player.deltaMovement = player.deltaMovement.add(right.scale(xMove.toDouble()))
    }

}
