package mod.chloeprime.elytrabooster.common.crafting

import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtils
import net.minecraft.potion.Potions
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModBrewingRecipe {
    @SubscribeEvent
    fun onCommonSetup(e: FMLCommonSetupEvent) {
        e.enqueueWork(::register)
    }

    private fun register() {
        /* 粘液球 + 力量1 -> 航空煤油 */
        BrewingRecipeRegistry.addRecipe(
            Ingredient.fromStacks(ItemStack(Items.POTION).also {
                PotionUtils.addPotionToItemStack(it, Potions.STRENGTH)
            }),
            Ingredient.fromItems(Items.SLIME_BALL),
            ModItems.JET_FUEL.get().defaultInstance
        )
    }
}