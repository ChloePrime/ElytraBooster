package mod.chloeprime.elytrabooster.common.util

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import java.lang.invoke.MethodType
import kotlin.math.roundToInt

internal const val MAX_ITEM_BAR_LENGTH = 13

internal val GET_TOOLTIP_LINES_SIGNATURE = MethodType.methodType(
    java.util.List::class.java,
    Player::class.java, TooltipFlag::class.java
)!!

internal fun toFullBarWidth(width01: Double) = (width01 * MAX_ITEM_BAR_LENGTH).roundToInt()

internal fun Item.getBarWidth01(stack: ItemStack) = getBarWidth(stack).toFloat() / MAX_ITEM_BAR_LENGTH

internal inline fun <reified T> findCapabilityKey(): Capability<T> {
    return CapabilityManager.get(object : CapabilityToken<T>() {});
}