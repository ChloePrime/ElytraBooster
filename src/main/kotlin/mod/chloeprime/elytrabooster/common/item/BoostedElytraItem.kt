package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.network.ModNetworking
import mod.chloeprime.elytrabooster.common.network.SEnergyUpdatePacket
import mod.chloeprime.elytrabooster.common.util.TextFormats
import mod.chloeprime.elytrabooster.common.util.plus
import mod.chloeprime.elytrabooster.common.util.translated
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.ServerPlayerEntity
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
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fml.network.PacketDistributor

/**
 * 需要消耗能源的推进鞘翅
 * @author ChloePrime
 */
open class BoostedElytraItem(
    properties: BoostedElytraProperties,
) : BoostedElytraItemBase(properties, properties.boostForce), IBoostedElytraItem {
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
    private val costFormula = properties.costFormula

    // 电力需求

    override fun canElytraFly(stack: ItemStack, entity: LivingEntity): Boolean {
        return (ENERGY_CAP?.let { cap ->
            stack.getCapability(cap).map {
                it.energyStored > 0
            }.orElse(false)
        } ?: false) && isUsable(stack)
    }

    // 耗电

    override fun elytraFlightTick(stack: ItemStack, entity: LivingEntity, flightTicks: Int): Boolean {
        return super.elytraFlightTick(stack, entity, flightTicks) && run {
            if (entity.world.isRemote) {
                return@run true
            }
            entity.getCapability(INPUT_CAP!!).map { input ->
                stack.getCapability(ENERGY_CAP!!).map {
                    extractEnergyAndUpdate(it, costFormula.applyAsInt(input), entity)
                    it.energyStored > 0
                }.orElse(false)
            }.orElse(true)
        }
    }

    /**
     * 消耗能源，
     * 然后向玩家发送网络包以刷新客户端显示
     */
    private fun extractEnergyAndUpdate(battery: IEnergyStorage, amount: Int, entity: LivingEntity): Int {
        return battery.extractEnergy(amount, false).also {
            if (amount > 0 && !entity.world.isRemote && entity is ServerPlayerEntity) {
                ModNetworking.CHANNEL.send(
                    PacketDistributor.PLAYER.with { entity },
                    SEnergyUpdatePacket(SLOT, battery.energyStored)
                )
            }
        }
    }

    // 储电

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ICapabilityProvider? {
        return CapProvider()
    }

    private inner class CapProvider : ICapabilitySerializable<IntNBT> {
        private var instance = createInstance(0)

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            return ENERGY_CAP!!.orEmpty(cap, instance)
        }

        override fun serializeNBT(): IntNBT {
            return IntNBT.valueOf(
                instance.map { it.energyStored }.orElse(0)
            )
        }

        override fun deserializeNBT(nbt: IntNBT?) {
            val energyInNbt = nbt?.int ?: return
            instance = createInstance(energyInNbt)
        }

        private fun createInstance(energy: Int) = LazyOptional.of<IEnergyStorage> {
            EnergyStorage(maxEnergy, maxEnergy, maxEnergy, energy)
        }
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
                it.receiveEnergy(it.maxEnergyStored, false)
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
                        it.energyStored, it.maxEnergyStored, 0x00FFFF,"FE"
                    )
                )
            }
        }
        if (stack.isDamageable) {
            val dur = stack.maxDamage - stack.damage
            tooltip.add(
                translated("elytra_booster.item.durability") + TextFormats.getProgressText(
                    dur, stack.maxDamage, 0xA0A0A0
                )
            )
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }
}
