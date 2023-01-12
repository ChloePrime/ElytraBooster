package mod.chloeprime.elytrabooster.common.event

import mod.chloeprime.elytrabooster.common.enchantment.EnergyLeakEnchantment
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.eventbus.api.Event

/**
 * 一般由内部使用
 * @see EnergyLeakEnchantment
 */
class ElytraCostEnergyEvent(
    val user: LivingEntity,
    val stack: ItemStack,
    var amount: Int
): Event()