package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.common.util.TextFormats
import mod.chloeprime.elytrabooster.common.util.translated
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
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
): Item(properties.maxStackSize(1)), IColoredItem {
    companion object {
        @JvmStatic
        @set:CapabilityInject(IFluidHandlerItem::class)
        var FLUID_CAP: Capability<IFluidHandlerItem>? = null
    }
    /**
     * 将物品颜色委托给流体颜色。
     */
    override val color get() = _fluid.get().attributes.color
    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider? {
        return if (javaClass == LiquidBottle::class.java) {
            /**
             * 魔改过的流体容器，
             * 瓶内液体始终不会发生变化。
             */
            object: FluidHandlerItemStackSimple.SwapEmpty(
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
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<ITextComponent>,
        flagIn: ITooltipFlag
    ) {
        FLUID_CAP?.let { cap ->
            stack.getCapability(cap).ifPresent {
                val fluid = it.getFluidInTank(0)
                tooltip.add(translated(
                    "elytra_booster.item.fuel.bottle.tooltip",
                    TextFormats.formatBigNumber(fluid.amount, -1),
                    _fluid.get().attributes.getDisplayName(fluid)
                ))
                tooltip.add(
                    translated("elytra_booster.item.fuel.bottle.tooltip.2")
                        .mergeStyle(TextFormatting.GRAY)
                )
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }
}
