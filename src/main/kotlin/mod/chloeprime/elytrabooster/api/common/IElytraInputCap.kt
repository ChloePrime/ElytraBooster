package mod.chloeprime.elytrabooster.api.common

import kotlin.math.abs

interface IElytraInputCap {
    var moveStrafe: Float
    var moveForward: Float

    val isBoosting: Boolean
        get() {
            return abs(moveForward) > 1e-5f
        }
}
