package mod.chloeprime.elytrabooster.common.enchantment

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.enchantment.Enchantment

/**
 * 闪避机动附魔
 */
class FlyDodgingEnchantment: Enchantment(
    Rarity.RARE,
    ModEnchantmentCategories.ELYTRAS,
    arrayOf(EquipmentSlot.CHEST)
) {
    override fun getMinCost(level: Int) = 9 + level * 20
    override fun getMaxCost(level: Int) = getMinCost(level) + 50
    override fun getMaxLevel() = 2
}