package mod.chloeprime.elytrabooster.common.item

import net.minecraft.world.item.ElytraItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial

/**
 * 可以用于鞘翅的护甲。
 */
open class ArmoredElytra(
    armorMaterial: ArmorMaterial,
    builder: Properties
): ArmorItem(armorMaterial, EquipmentSlot.CHEST, builder) {

    override fun isEnchantable(stack: ItemStack): Boolean {
        return true
    }

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return ElytraItem.isFlyEnabled(stack)
    }

    override fun elytraFlightTick(stack: ItemStack, entity: LivingEntity, flightTicks: Int): Boolean {
        return true
    }
}