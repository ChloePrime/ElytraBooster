package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod.MODID
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.item.IArmorMaterial
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.SoundEvent
import net.minecraft.util.SoundEvents

enum class ModArmorMaterials(
    private val name_: String,
    private val defense: Int,
    private val enchantability: Int,
    private val soundEvent: SoundEvent,
    private val toughness: Float,
    private val knockbackResistance: Float
) : IArmorMaterial {
    FUEL_ARMORS("${MODID}:fuel_armors", 4, 10,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0f, 0f),
    ELECTRIC_1("${MODID}:electric_1", 8, 15,
        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, 0.1f),
    ELECTRIC_2("${MODID}:electric_2", 12, 20,
        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4f, 0.2f),
    CREATIVE_ELYTRA("${MODID}:creative", 255, 500,
        SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 25f, 0f);

    override fun getDurability(slotIn: EquipmentSlotType) = 0
    override fun getRepairMaterial() = Ingredient.EMPTY!!

    override fun getDamageReductionAmount(slotIn: EquipmentSlotType) = defense
    override fun getEnchantability() = enchantability
    override fun getSoundEvent() = soundEvent
    override fun getName() = name_
    override fun getToughness() = toughness
    override fun getKnockbackResistance() = knockbackResistance
}