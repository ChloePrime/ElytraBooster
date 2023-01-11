package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.common.util.TextFormats
import mod.chloeprime.elytrabooster.common.util.findCapabilityKey
import mod.chloeprime.elytrabooster.common.util.translated
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple
import java.util.function.Supplier

private const val BOTTLE_VOLUME = 250
private val EMPTY_BOTTLE = ItemStack(Items.GLASS_BOTTLE)

open class LiquidBottle(
    properties: Properties,
    private val _fluid: Supplier<out Fluid>
) : Item(properties.stacksTo(1)), IColoredItem {
    companion object {
        var FLUID_CAP: Capability<IFluidHandlerItem> = findCapabilityKey()
    }

    /**
     * 将物品颜色委托给流体颜色。
     */
    override val color get() = _fluid.get().attributes.color
    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? {
        return if (javaClass == LiquidBottle::class.java) {
            /**
             * 魔改过的流体容器，
             * 瓶内液体始终不会发生变化。
             */
            object : FluidHandlerItemStackSimple.SwapEmpty(
                stack, EMPTY_BOTTLE.copy(), BOTTLE_VOLUME
            ) {
                override fun getFluid(): FluidStack {
                    return FluidStack(_fluid.get(), capacity)
                }
            }
        } else {
            super.initCapabilities(stack, nbt)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun appendHoverText(
        stack: ItemStack,
        worldIn: Level?,
        tooltip: MutableList<Component>,
        flagIn: TooltipFlag
    ) {
        stack.getCapability(FLUID_CAP).ifPresent {
            val fluid = it.getFluidInTank(0)
            tooltip.add(
                translated(
                    "elytra_booster.item.fuel.bottle.tooltip",
                    TextFormats.formatBigNumber(fluid.amount, -1),
                    _fluid.get().attributes.getDisplayName(fluid)
                )
            )
            tooltip.add(
                translated("elytra_booster.item.fuel.bottle.tooltip.2")
                    .withStyle(ChatFormatting.GRAY)
            )
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
    }
}
