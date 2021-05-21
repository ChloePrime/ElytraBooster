package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Vector3d

/**
 * 使用快速开根号算法求单位向量
 */
fun Vector3d.fastNormalize(): Vector3d {
    val invLength = MathHelper.fastInvSqrt(x * x + y * y + z * z)
    return if (invLength < 1e-4) {
        Vector3d.ZERO
    } else {
        Vector3d(x * invLength,y * invLength,z * invLength)
    }
}

fun Vector3d.fastLength() = 1 / MathHelper.fastInvSqrt(x * x + y * y + z * z)

fun Vector3d.withLength(length: Double): Vector3d {
    return if (length < 1e-4) {
        Vector3d.ZERO
    } else {
        // length / curLength
        val scale = length * MathHelper.fastInvSqrt(x * x + y * y + z * z)
        Vector3d(
            x * scale,
            y * scale,
            z * scale
        )
    }
}
