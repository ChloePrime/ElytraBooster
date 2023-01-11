package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object ModItemGroup: CreativeModeTab(ElytraBoosterMod.MODID) {
    override fun makeIcon(): ItemStack = ItemStack(ModItems.CREATIVE_BOOSTED_ELYTRA.get())
}