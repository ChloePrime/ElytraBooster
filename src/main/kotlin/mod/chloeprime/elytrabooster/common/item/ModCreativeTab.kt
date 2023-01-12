package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.enchantment.ModEnchantmentCategories
import mod.chloeprime.elytrabooster.common.enchantment.ModEnchantments
import net.minecraft.core.NonNullList
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentInstance

object ModCreativeTab: CreativeModeTab(ElytraBoosterMod.MODID) {
    override fun makeIcon(): ItemStack = ItemStack(ModItems.CREATIVE_BOOSTED_ELYTRA.get())

    override fun fillItemList(list: NonNullList<ItemStack>) {
        super.fillItemList(list)

        ModEnchantments.REGISTRY.entries.forEach {
            val ench = it.get()
            list.add(EnchantedBookItem.createForEnchantment(EnchantmentInstance(ench, ench.maxLevel)))
        }
    }
}