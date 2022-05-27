package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.config.FuelElytraConfigEntry
import mod.chloeprime.elytrabooster.common.util.TextFormats
import mod.chloeprime.elytrabooster.common.util.plus
import mod.chloeprime.elytrabooster.common.util.translated
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.IArmorMaterial
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.NonNullList
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack
import java.util.function.Supplier

/**
 * 消耗燃油的推进鞘翅
 * @author ChloePrime
 */
open class FuelBoostedElytraItem(
    armorMaterial: IArmorMaterial,
    properties: Properties,
) : BoostedElytraItemBase(armorMaterial, properties, properties.boostForce), IBoostedElytraItem {

    open class Properties(
        var fuelType: Supplier<out Fluid>,
    ) : BoostedElytraProperties<FuelElytraConfigEntry>() {
        constructor() : this(Fluids.EMPTY.delegate)
    }

    companion object {
        const val DURABILITY_BAR_HUE = 21F / 180

        @JvmStatic
        @set:CapabilityInject(IFluidHandlerItem::class)
        var FLUID_CAP: Capability<IFluidHandlerItem>? = null

        @JvmStatic
        @set:CapabilityInject(IElytraInputCap::class)
        var INPUT_CAP: Capability<IElytraInputCap>? = null
    }

    private val maxEnergy = properties.maxEnergy
    private val fuelType = properties.fuelType
    private val costFormula = properties.costFormula

    // 电力需求

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return (FLUID_CAP?.let { cap ->
            stack.getCapability(cap).map {
                it.getFluidInTank(0).amount > 0
            }.orElse(false)
        } ?: false)
    }

    // 耗电

    override fun elytraFlightTick(
        stack: ItemStack,
        entity: LivingEntity,
        flightTicks: Int
    ): Boolean {
        if (entity.world.isRemote) {
            return true
        }
        return entity.getCapability(INPUT_CAP!!).map { input ->
            stack.getCapability(FLUID_CAP!!).map {
                it.drain(
                    costFormula.applyAsInt(input),
                    IFluidHandler.FluidAction.EXECUTE
                ).amount > 0
            }.orElse(false)
        }.orElse(true)
    }

    // 储电

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider? {
        /**
         * 限制内容物类型的流体容器（限制为燃料）
         */
        return object : FluidHandlerItemStack(stack, maxEnergy.asInt) {
            override fun canFillFluidType(fluid: FluidStack) =
                fluid.fluid == fuelType.get()

            override fun canDrainFluidType(fluid: FluidStack) =
                fluid.fluid == fuelType.get()
        }
    }

    // 让耐久条显示电力情况

    override fun showDurabilityBar(stack: ItemStack?) = true
    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        val dur = 1F - getDurabilityForDisplay(stack).toFloat()
        return MathHelper.hsvToRGB(DURABILITY_BAR_HUE, dur, 0.25F * dur + 0.75F)
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        return stack.getCapability(FLUID_CAP!!).map {
            1 - it.getFluidInTank(0).amount.toDouble() / it.getTankCapacity(0)
        }.orElse(0.0)
    }

    /**
     * 把物品加入创造标签栏，同时加入一个满电的版本
     */
    override fun fillItemGroup(group: ItemGroup, items: NonNullList<ItemStack>) {
        super.fillItemGroup(group, items)
        if (!isInGroup(group)) return

        val fullEnergyStack = defaultInstance
        FLUID_CAP?.let { cap ->
            fullEnergyStack.getCapability(cap).ifPresent {
                it.fill(
                    FluidStack(fuelType.get(), it.getTankCapacity(0)),
                    IFluidHandler.FluidAction.EXECUTE
                )
                items.add(fullEnergyStack)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<ITextComponent>,
        flagIn: ITooltipFlag
    ) {
        if (FLUID_CAP != null) {
            stack.getCapability(FLUID_CAP!!).ifPresent {
                tooltip.add(getFuelTooltip(it))
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    private fun getFuelTooltip(fuelTank: IFluidHandler): ITextComponent {
        return translated("elytra_booster.item.fuel") +
                TextFormats.getProgressText(
                    fuelTank.getFluidInTank(0).amount,
                    fuelTank.getTankCapacity(0),
                    0xFFA500, "L"
                ) +
                // 介词，中文语言下为一个空格
                translated("elytra_booster.item.fuel.prep") +
                // 流体名称
                fuelType.get().attributes.getDisplayName(
                    fuelTank.getFluidInTank(0)
                )
    }
}
