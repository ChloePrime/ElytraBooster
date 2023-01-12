package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.config.FuelElytraConfigEntry
import mod.chloeprime.elytrabooster.common.util.*
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
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
    armorMaterial: ArmorMaterial,
    properties: Properties,
) : BoostedElytraItemBase(armorMaterial, properties, properties.boostForce), IBoostedElytraItem {

    open class Properties(
        var fuelType: Supplier<out Fluid>,
    ) : BoostedElytraProperties<FuelElytraConfigEntry>() {
        constructor() : this(Fluids.EMPTY.delegate)
    }

    companion object {
        const val DURABILITY_BAR_HUE = 21F / 180

        var FLUID_CAP: Capability<IFluidHandlerItem> = findCapabilityKey()

        var INPUT_CAP: Capability<IElytraInputCap> = findCapabilityKey()
    }

    private val maxEnergy = properties.maxEnergy
    private val fuelType = properties.fuelType
    private val costFormula = properties.costFormula

    // 电力需求

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return stack.getCapability(FLUID_CAP).map {
            it.getFluidInTank(0).amount > 0
        }.orElse(false)
    }

    // 耗电

    override fun elytraFlightTick(
        stack: ItemStack,
        entity: LivingEntity,
        flightTicks: Int
    ): Boolean {
        if (entity.level.isClientSide) {
            return true
        }
        return entity.getCapability(INPUT_CAP).map { input ->
            stack.getCapability(FLUID_CAP).map {
                val cost = modifyCost(entity, stack, costFormula.applyAsInt(input))
                it.drain(
                    cost,
                    IFluidHandler.FluidAction.EXECUTE
                ).amount > 0
            }.orElse(false)
        }.orElse(true)
    }

    // 储电

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? {
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

    override fun isBarVisible(stack: ItemStack) = true
    override fun getBarColor(stack: ItemStack): Int {
        val dur = getBarWidth01(stack)
        return Mth.hsvToRgb(DURABILITY_BAR_HUE, dur, 0.25F * dur + 0.75F)
    }

    override fun getBarWidth(stack: ItemStack): Int {
        val width01 = stack.getCapability(FLUID_CAP).map {
            it.getFluidInTank(0).amount.toDouble() / it.getTankCapacity(0)
        }.orElse(0.0)
        return toFullBarWidth(width01)
    }

    /**
     * 把物品加入创造标签栏，同时加入一个满电的版本
     */
    override fun fillItemCategory(group: CreativeModeTab, items: NonNullList<ItemStack>) {
        super.fillItemCategory(group, items)
        if (!allowdedIn(group)) return

        val fullEnergyStack = defaultInstance
        fullEnergyStack.getCapability(FLUID_CAP).ifPresent {
            it.fill(
                FluidStack(fuelType.get(), it.getTankCapacity(0)),
                IFluidHandler.FluidAction.EXECUTE
            )
            items.add(fullEnergyStack)
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
            tooltip.add(getFuelTooltip(it))
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
    }

    private fun getFuelTooltip(fuelTank: IFluidHandler): Component {
        return translated("elytra_booster.item.fuel") +
                TextFormats.getProgressText(
                    fuelTank.getFluidInTank(0).amount,
                    fuelTank.getTankCapacity(0),
                    0xFFA500,
                    "B", -1
                ) +
                // 介词，中文语言下为一个空格
                translated("elytra_booster.item.fuel.prep") +
                // 流体名称
                fuelType.get().attributes.getDisplayName(
                    fuelTank.getFluidInTank(0)
                )
    }
}
