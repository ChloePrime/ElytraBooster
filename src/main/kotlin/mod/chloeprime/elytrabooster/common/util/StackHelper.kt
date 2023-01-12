package mod.chloeprime.elytrabooster.common.util

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.TooltipFlag
import java.lang.invoke.MethodType

object StackHelper {
    val GET_TOOLTIP_LINES_SIGNATURE = MethodType.methodType(
        java.util.List::class.java,
        Player::class.java, TooltipFlag::class.java
    )!!
}