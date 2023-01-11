package mod.chloeprime.elytrabooster.client.item

import mod.chloeprime.elytrabooster.client.model.NullHumanoidModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.IItemRenderProperties

object ElytraBaseRenderProperties: IItemRenderProperties {
    override fun getArmorModel(
        entityLiving: LivingEntity?,
        itemStack: ItemStack?,
        armorSlot: EquipmentSlot?,
        original: HumanoidModel<*>?
    ): HumanoidModel<*> {
        return NullHumanoidModel.instance<LivingEntity>()
    }
}