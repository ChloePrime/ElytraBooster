package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isFlyingWithBooster
import mod.chloeprime.elytrabooster.common.util.Time
import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.abs

/**
 * 鞘翅飞行时按左右键旋转镜头
 * 是特效，同时也控制旋转速度。
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
object CameraRoll {
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

    private val MINECRAFT = Minecraft.getInstance()

    /**
     * 当前视角倾斜占倾斜上限的百分比
     */
    val rollRate get() = abs(roll / MAX_ROLL_ANGLE)

    /**
     * 此处处理roll计算
     * 涉及到旋转速度这一玩法因素
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onRenderTick(e: TickEvent.RenderTickEvent) {
        if (e.phase === TickEvent.Phase.END || MINECRAFT.isPaused) return

        val player = MINECRAFT.player ?: return

        targetRoll = if (!player.isFlyingWithBooster) {
            0.0F
        } else {
            val input = ElytraBoosterApi.getElytraInputOrNull(player) ?: return
            -input.moveStrafe * MAX_ROLL_ANGLE
        }

        roll += (targetRoll - roll) * getActualSmoothSpeed(player)
    }

    /**
     * 特效
     * 此处不涉及玩法
     */
    @SubscribeEvent
    fun onCameraSetup(e: EntityViewRenderEvent.CameraSetup) {
        if (MINECRAFT.level == null) return
        if (MINECRAFT.options.cameraType === CameraType.FIRST_PERSON) {
            if (abs(roll) > 1e-2f) {
                e.roll += roll
            }
        }
    }

    private fun getActualSmoothSpeed(player: Player): Float {
        val groundBonus = 3
        return Mth.clamp(
            if (player.isFallFlying) {
                SMOOTH_SPEED
            } else {
                SMOOTH_SPEED * groundBonus
            } * Time.deltaTime,
            0F, 1F
        )
    }
}
