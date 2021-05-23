package mod.chloeprime.elytrabooster.common.crafting

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.common.brewing.BrewingRecipe
import java.util.function.Supplier

class LazyBrewingRecipe(
    private val input: Supplier<Ingredient>,
    private val ingredient: Supplier<Ingredient>,
    private val output: Supplier<ItemStack>
): BrewingRecipe(Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY) {
    /* IBrewingRecipe的实现 */

    override fun isInput(stack: ItemStack): Boolean {
        return input.get().test(stack)
    }

    override fun isIngredient(stack: ItemStack): Boolean {
        return ingredient.get().test(stack)
    }

    override fun getOutput(input: ItemStack, ingredient: ItemStack): ItemStack {
        return if (isInput(input) && isIngredient(ingredient)) {
            output.get().copy()
        } else {
            ItemStack.EMPTY
        }
    }

    /* BrewingRecipe的重写 */

    override fun getInput() = input.get()
    override fun getIngredient() = ingredient.get()
    override fun getOutput() = output.get()
}