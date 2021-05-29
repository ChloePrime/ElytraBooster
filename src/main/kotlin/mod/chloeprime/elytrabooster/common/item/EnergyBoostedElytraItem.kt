package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.caps.ISettableEnergyStorage
import mod.chloeprime.elytrabooster.common.caps.energy
import mod.chloeprime.elytrabooster.common.config.FeElytraConfigEntry
import mod.chloeprime.elytrabooster.common.util.TextFormats
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.IntNBT
import net.minecraft.util.Direction
import net.minecraft.util.NonNullList
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import java.util.function.IntSupplier
import kotlin.math.min

/**
 * 消耗FE能源的推进鞘翅
 * @author ChloePrime
 */
open class EnergyBoostedElytraItem(
    properties: Properties,
) : BoostedElytraItemBase(properties, properties.boostForce), IBoostedElytraItem {
    open class Properties(
        var chargeSpeed: IntSupplier,
    ): BoostedElytraProperties<FeElytraConfigEntry>() {
        constructor() : this({ 0 })

        fun chargeSpeed(chargeSpeed: IntSupplier): Properties {
            this.chargeSpeed = chargeSpeed
            return this
        }

        override fun acceptConfig(config: FeElytraConfigEntry): Properties {
            super.acceptConfig(config)
            chargeSpeed { config.chargeSpeed.get() }
            return this
        }
    }
    companion object {
        const val DURABILITY_BAR_HUE = 0.5f

        @JvmStatic
        @set:CapabilityInject(IEnergyStorage::class)
        var ENERGY_CAP: Capability<IEnergyStorage>? = null

        @JvmStatic
        @set:CapabilityInject(IElytraInputCap::class)
        var INPUT_CAP: Capability<IElytraInputCap>? = null
    }

    private val maxEnergy = properties.maxEnergy
    private val chargeSpeed = properties.chargeSpeed
    private val costFormula = properties.costFormula

    // 电力需求

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return (ENERGY_CAP?.let { cap ->
            stack.getCapability(cap).map {
                it.energyStored > 0
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
            stack.getCapability(ENERGY_CAP!!).map {
                it.energy -= costFormula.applyAsInt(input)
                it.energyStored > 0
            }.orElse(false)
        }.orElse(true)
    }

    // 储电

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider? {
        return CapProvider(stack)
    }

    private inner class CapProvider(
        private val stack: ItemStack
    ) : ICapabilityProvider, ISettableEnergyStorage {
        private var instance = LazyOptional.of { this }

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            return ENERGY_CAP!!.orEmpty(cap, instance.cast())
        }

        override fun getEnergyStored(): Int {
            val nbt = stack.orCreateTag["fe"]
            return if (nbt is IntNBT) {
                nbt.int
            } else {
                stack.orCreateTag.putInt("fe", 0)
                0
            }
        }

        override fun setEnergyStored(value: Int) {
            stack.orCreateTag.putInt("fe", value)
        }

        override fun receiveEnergy(given: Int, simulate: Boolean): Int {
            val current = energyStored
            val actualReceive = minOf(given, maxEnergyStored - current, chargeSpeed.asInt)
            if (!simulate) {
                energyStored = current + actualReceive
            }
            return actualReceive
        }

        override fun extractEnergy(request: Int, simulate: Boolean): Int {
            val current = energyStored
            val actualExtract = min(request, current)
            if (!simulate) {
                energyStored = current - actualExtract
            }
            return actualExtract
        }

        override fun getMaxEnergyStored(): Int = maxEnergy.asInt

        override fun canExtract(): Boolean = false

        override fun canReceive(): Boolean = true

    }

    // 让耐久条显示电力情况

    override fun showDurabilityBar(stack: ItemStack?) = true
    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        val dur = 1F - getDurabilityForDisplay(stack).toFloat()
        return MathHelper.hsvToRGB(DURABILITY_BAR_HUE, dur, 0.25F * dur + 0.75F)
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        return stack.getCapability(ENERGY_CAP!!).map {
            1F - it.energyStored.toDouble() / it.maxEnergyStored
        }.orElse(0.0)
    }

    /**
     * 把物品加入创造标签栏，同时加入一个满电的版本
     */
    override fun fillItemGroup(group: ItemGroup, items: NonNullList<ItemStack>) {
        super.fillItemGroup(group, items)
        if (!isInGroup(group)) return

        val fullEnergyStack = defaultInstance
        ENERGY_CAP?.let { cap ->
            fullEnergyStack.getCapability(cap).ifPresent {
                it.energy = it.maxEnergyStored
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
        if (ENERGY_CAP != null) {
            stack.getCapability(ENERGY_CAP!!).ifPresent {
                tooltip.add(
                    TextFormats.getProgressText(
                        it.energyStored, it.maxEnergyStored, 0x00FFFF, " FE"
                    )
                )
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }
}
