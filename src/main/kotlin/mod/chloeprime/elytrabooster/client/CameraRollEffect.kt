package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.common.util.Time
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.PointOfView
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/**
 * 鞘翅飞行时按左右键旋转镜头
 * 只是个特效而已
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
object CameraRollEffect {
    /**
     * 单位为角度（Degrees）
     */
    private const val MAX_ROLL_ANGLE = 45

    /**
     * TODO 将平滑系数改为可配置值
     */
    private const val SMOOTH_SPEED = 0.1F
    private var roll = 0.0F
    private var targetRoll = 0.0F

    @SubscribeEvent
    fun onCameraSetup(e: EntityViewRenderEvent.CameraSetup) {
        val mc = Minecraft.getInstance()
        if (mc.gameSettings.pointOfView != PointOfView.FIRST_PERSON) return
        val player = mc.player ?: return

        targetRoll = if (!ElytraBoosterApi.isFlyingWithBooster(player)) {
            0.0F
        } else {
            val input = ElytraBoosterApi.getElytraInputOrNull(player) ?: return
            -input.moveStrafe * MAX_ROLL_ANGLE
        }

        roll += (targetRoll - roll) * getActualSmoothSpeed(player)
        e.roll += roll
    }

    private fun getActualSmoothSpeed(player: PlayerEntity): Float {
        val groundBonus = 3
        return MathHelper.clamp(
            if (player.isElytraFlying) {
                SMOOTH_SPEED
            } else {
                SMOOTH_SPEED * groundBonus
            } * Time.deltaTime,
            0F, 1F
        )
    }
}
