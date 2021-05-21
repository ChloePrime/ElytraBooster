package mod.chloeprime.elytrabooster.common.util

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.vector.Vector3d

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
    const val RESISTANCE_FACTOR = (0.5F * 0.2F * 1.2258F * 2 / 50F) / 400F * 540F

    fun getAccelerationForGoalSpeed(goalSpeed: Float): Float {
        return goalSpeed * goalSpeed * RESISTANCE_FACTOR
    }

    fun getAccelerationForPlayer(player: PlayerEntity): Float {
        return getAccelerationForGoalSpeed(
            player.getAttributeValue(ElytraBoosterApi.Attributes.BOOST_SPEED.get()).toFloat()
        )
    }

    fun getAngularAcceleration(motion: Vector3d): Float {
        val speed = motion.fastLength().toFloat() + 1
        // TODO 将角加速度变为Attribute
        return Math.PI.toFloat() / 4 / (speed * speed)
    }
}
