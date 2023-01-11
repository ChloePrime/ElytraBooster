package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

/**
 * 使用快速开根号算法求单位向量
 */
fun Vec3.fastNormalize(): Vec3 {
    val invLength = Mth.fastInvSqrt(x * x + y * y + z * z)
    return if (invLength < 1e-4) {
        Vec3.ZERO
    } else {
        Vec3(x * invLength,y * invLength,z * invLength)
    }
}

fun Vec3.fastLength() = 1 / Mth.fastInvSqrt(x * x + y * y + z * z)

fun Vec3.withLength(length: Double): Vec3 {
    return if (length < 1e-4) {
        Vec3.ZERO
    } else {
        // length / curLength
        val scale = length * Mth.fastInvSqrt(x * x + y * y + z * z)
        Vec3(
            x * scale,
            y * scale,
            z * scale
        )
    }
}
