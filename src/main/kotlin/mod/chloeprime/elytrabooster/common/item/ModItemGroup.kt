package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object ModItemGroup: ItemGroup(ElytraBoosterMod.MODID) {
    override fun createIcon(): ItemStack = ItemStack(ModItems.CREATIVE_BOOSTED_ELYTRA.get())
}