package mod.chloeprime.elytrabooster.common.crafting

import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.PotionUtils
import net.minecraft.potion.Potions
import net.minecraft.tags.ItemTags
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModBrewingRecipe {
    private val recipes = arrayOf(
        /* 煤炭/木炭 + 力量1 -> 航空煤油 */
        Triple(Potions.STRENGTH, Ingredient.fromTag(ItemTags.COALS), lazy { ModItems.JET_FUEL.get().defaultInstance }),
        /* 龙息 + 力量2 -> 固体火箭燃料 */
        Triple(Potions.STRONG_STRENGTH, Ingredient.fromItems(Items.DRAGON_BREATH), lazy { ModItems.ROCKET_FUEL.get().defaultInstance }),
    )

    @SubscribeEvent
    fun onCommonSetup(e: FMLCommonSetupEvent) {
        e.enqueueWork(::register)
    }

    private fun register() {
        recipes.forEach { (potion, ingredient, lazyResult) ->
            BrewingRecipeRegistry.addRecipe(
                Ingredient.fromStacks(ItemStack(Items.POTION).also {
                    PotionUtils.addPotionToItemStack(it, potion)
                }),
                ingredient,
                lazyResult.value
            )
        }

    }
}