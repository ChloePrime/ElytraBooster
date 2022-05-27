package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.client.model.EmptyBipedModel
import net.minecraft.client.renderer.entity.model.BipedModel
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.item.ArmorItem
import net.minecraft.item.ElytraItem
import net.minecraft.item.IArmorMaterial
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * 可以用于鞘翅的护甲。
 */
open class ArmoredElytra(
    armorMaterial: IArmorMaterial,
    builder: Properties
): ArmorItem(armorMaterial, EquipmentSlotType.CHEST, builder) {

    override fun isEnchantable(stack: ItemStack): Boolean {
        return true
    }

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return ElytraItem.isUsable(stack)
    }

    override fun elytraFlightTick(stack: ItemStack, entity: LivingEntity, flightTicks: Int): Boolean {
        return true
    }

    @Suppress("UNCHECKED_CAST")
    @OnlyIn(Dist.CLIENT)
    override fun <A : BipedModel<*>> getArmorModel(
        entityLiving: LivingEntity?,
        itemStack: ItemStack?,
        armorSlot: EquipmentSlotType?,
        _default: A
    ): A? {
        return EmptyBipedModel<PlayerEntity>() as A
    }
}