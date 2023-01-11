package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.caps.ISettableEnergyStorage
import mod.chloeprime.elytrabooster.common.caps.energy
import mod.chloeprime.elytrabooster.common.config.FeElytraConfigEntry
import mod.chloeprime.elytrabooster.common.util.TextFormats
import mod.chloeprime.elytrabooster.common.util.getBarWidth01
import mod.chloeprime.elytrabooster.common.util.findCapabilityKey
import mod.chloeprime.elytrabooster.common.util.toFullBarWidth
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.capabilities.Capability
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
    armorMaterial: ArmorMaterial,
    properties: Properties,
) : BoostedElytraItemBase(armorMaterial, properties, properties.boostForce), IBoostedElytraItem {

    open class Properties(
        var chargeSpeed: IntSupplier,
    ) : BoostedElytraProperties<FeElytraConfigEntry>() {
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

        var ENERGY_CAP: Capability<IEnergyStorage> = findCapabilityKey()

        var INPUT_CAP: Capability<IElytraInputCap> = findCapabilityKey()
    }

    private val maxEnergy = properties.maxEnergy
    private val chargeSpeed = properties.chargeSpeed
    private val costFormula = properties.costFormula

    // 电力需求

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return stack.getCapability(ENERGY_CAP).map {
            it.energyStored > 0
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
            stack.getCapability(ENERGY_CAP).map {
                it.energy -= costFormula.applyAsInt(input)
                it.energyStored > 0
            }.orElse(false)
        }.orElse(true)
    }

    // 储电

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? {
        return CapProvider(stack)
    }

    private inner class CapProvider(
        private val stack: ItemStack
    ) : ICapabilityProvider, ISettableEnergyStorage {
        private var instance = LazyOptional.of { this }

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            return ENERGY_CAP.orEmpty(cap, instance.cast())
        }

        override fun getEnergyStored(): Int {
            val nbt = stack.orCreateTag["fe"]
            return if (nbt is IntTag) {
                nbt.asInt
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

    override fun isBarVisible(stack: ItemStack) = true
    override fun getBarColor(stack: ItemStack): Int {
        val dur = getBarWidth01(stack)
        return Mth.hsvToRgb(DURABILITY_BAR_HUE, dur, 0.25F * dur + 0.75F)
    }

    override fun getBarWidth(stack: ItemStack): Int {
        val width01 = stack.getCapability(ENERGY_CAP).map {
            it.energyStored.toDouble() / it.maxEnergyStored
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
        fullEnergyStack.getCapability(ENERGY_CAP).ifPresent {
            it.energy = it.maxEnergyStored
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
        stack.getCapability(ENERGY_CAP).ifPresent {
            tooltip.add(
                TextFormats.getProgressText(
                    it.energyStored, it.maxEnergyStored, 0x00FFFF, "FE"
                )
            )
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
    }
}
