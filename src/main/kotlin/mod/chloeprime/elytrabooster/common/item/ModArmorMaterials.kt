package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod.MODID
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient

enum class ModArmorMaterials(
    private val name_: String,
    private val defense: Int,
    private val enchantability: Int,
    private val soundEvent: SoundEvent,
    private val toughness: Float,
    private val knockbackResistance: Float
) : ArmorMaterial {
    FUEL_ARMORS("${MODID}:fuel_armors", 4, 10,
        SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f),
    ELECTRIC_1("${MODID}:electric_1", 8, 15,
        SoundEvents.ARMOR_EQUIP_NETHERITE, 3f, 0.1f),
    ELECTRIC_2("${MODID}:electric_2", 12, 20,
        SoundEvents.ARMOR_EQUIP_NETHERITE, 4f, 0.2f),
    CREATIVE_ELYTRA("${MODID}:creative", 255, 500,
        SoundEvents.ARMOR_EQUIP_ELYTRA, 25f, 0f);

    override fun getDurabilityForSlot(slotIn: EquipmentSlot) = 0
    override fun getRepairIngredient(): Ingredient = Ingredient.EMPTY

    override fun getDefenseForSlot(pSlot: EquipmentSlot)= defense
    override fun getEnchantmentValue() = enchantability
    override fun getEquipSound() = soundEvent
    override fun getName() = name_
    override fun getToughness() = toughness
    override fun getKnockbackResistance() = knockbackResistance
}