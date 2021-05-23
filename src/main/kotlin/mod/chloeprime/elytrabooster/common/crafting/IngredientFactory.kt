package mod.chloeprime.elytrabooster.common.crafting

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.common.crafting.NBTIngredient

object IngredientFactory {
    fun newNBTIngredient(stack: ItemStack): Ingredient =
        object : NBTIngredient(stack) {}
}
