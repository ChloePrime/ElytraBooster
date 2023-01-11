package mod.chloeprime.elytrabooster.common.crafting

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.crafting.NBTIngredient

object IngredientFactory {
    fun newNBTIngredient(stack: ItemStack): Ingredient = object : NBTIngredient(stack) {}
}
