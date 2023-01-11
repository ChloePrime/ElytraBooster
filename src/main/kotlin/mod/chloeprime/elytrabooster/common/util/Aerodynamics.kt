package mod.chloeprime.elytrabooster.common.util

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

object Aerodynamics {
    /**
     * 空气阻力前面的常数 k。
     * 乘上这个值之后，剩下的影响加速度的因素就只与速度有关了
     * 空气阻力系数取 0.2，
     * 空气密度 1.2258，
     * 迎风面积 2，
     * 质量取 50kg，
     * 此时的单位为m/s^2。
     * 换算到 m/tick^2 (1/400倍之于m/s^2)，
     * 最后一个 1000 是根据实操手感调整出来的缩放倍率，
     * 设计意图是让2.0推力勉强能够爬升。
     *
     * 实际上，由于设计意图，推力加速度与阻力系数有关，
     * 而当阻力系数过小，使得推力过小时，推进加速度将被原版的空气阻力覆盖，
     * 导致无法爬升。
     */
    private const val AIR_DRAG_BASE = (0.5F * 0.2F * 1.2258F * 2 / 50F) / 400F * 540F

    /**
     * 为了好看，Attributes 使用的单位与物理学国际单位间有一定的缩放比例，此数为字面量的放大倍率。
     */
    private const val RPG_TO_PHYSICS_UNIT_SCALE = 2.5f

    val AIR_DRAG by lazy {
        AIR_DRAG_BASE * ElyBoosterModConfig.AIR_DRAG_SCALE.get().toFloat()
    }

    fun getAccelerationForGoalSpeed(goalSpeed: Float): Float {
        return goalSpeed * goalSpeed * AIR_DRAG
    }

    fun getAccelerationForPlayer(player: Player): Float {
        val attribute = player.getAttributeValue(ElytraBoosterApi.Attributes.BOOST_FORCE.get()).toFloat()
        return getAccelerationForGoalSpeed(attribute / RPG_TO_PHYSICS_UNIT_SCALE)
    }

    /**
     * 标准转弯速度，
     * 取 30°/s （由于转弯操作会乘上Time.deltaTime，所以时间单位为秒）
     */
    private const val STANDARD_ROTATE_SPEED = Math.PI.toFloat() / 6

    /**
     * @param force 推进力数值，单位为 RPG 单位（使用时需除以 2.5）
     */
    fun getAngularAcceleration(motion: Vec3, force: Float): Float {
        return STANDARD_ROTATE_SPEED * motion.fastLength().toFloat() / (force / RPG_TO_PHYSICS_UNIT_SCALE)
    }
}
