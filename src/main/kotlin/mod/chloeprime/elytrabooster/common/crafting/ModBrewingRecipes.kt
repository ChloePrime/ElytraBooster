package mod.chloeprime.elytrabooster.common.crafting

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModBrewingRecipes {
    private val recipes = arrayOf(
        /* 煤炭/木炭 + 力量1 -> 航空煤油 */
        Triple(Potions.LONG_SWIFTNESS, lazy { Ingredient.of(ItemTags.COALS) }, lazy { ModItems.JET_FUEL.get().defaultInstance }),
        /* 龙息 + 力量2 -> 固体火箭燃料 */
        Triple(Potions.STRONG_STRENGTH, lazyOf(Ingredient.of(Items.DRAGON_BREATH)), lazy { ModItems.ROCKET_FUEL.get().defaultInstance }),
    )

    @SubscribeEvent
    fun onCommonSetup(e: FMLCommonSetupEvent) {
        e.enqueueWork(::register)
    }

    private fun register() {
        recipes.forEach { (potion, lazyIngredient, lazyResult) ->
            try {
                BrewingRecipeRegistry.addRecipe(
                    LazyBrewingRecipe({
                        IngredientFactory.newNBTIngredient(ItemStack(Items.POTION).also {
                            PotionUtils.setPotion(it, potion)
                        })
                    }, { lazyIngredient.value }, { lazyResult.value })
                )
            } catch (e: Exception) {
                ElytraBoosterMod.LOGGER.error("Error registering brew recipe ${lazyResult.value.item.registryName}", e)
            }
        }
    }
}