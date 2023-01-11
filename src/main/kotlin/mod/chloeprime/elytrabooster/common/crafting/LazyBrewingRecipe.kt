package mod.chloeprime.elytrabooster.common.crafting

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.brewing.BrewingRecipe
import java.util.function.Supplier

class LazyBrewingRecipe(
    input: Supplier<Ingredient>,
    ingredient: Supplier<Ingredient>,
    output: Supplier<ItemStack>
): BrewingRecipe(Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY) {
    /* IBrewingRecipe的实现 */
    private val inputValue by lazy { input.get() }
    private val ingredientValue by lazy { ingredient.get() }
    private val outputValue by lazy { output.get() }

    override fun isInput(stack: ItemStack): Boolean {
        return inputValue.test(stack)
    }

    override fun isIngredient(stack: ItemStack): Boolean {
        return ingredientValue.test(stack)
    }

    override fun getOutput(input: ItemStack, ingredient: ItemStack): ItemStack {
        return if (isInput(input) && isIngredient(ingredient)) {
            outputValue.copy()
        } else {
            ItemStack.EMPTY
        }
    }

    /* BrewingRecipe的重写 */

    override fun getInput() = inputValue
    override fun getIngredient() = ingredientValue
    override fun getOutput() = outputValue
}