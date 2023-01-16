package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.common.event.ElytraCostEnergyEvent
import mod.chloeprime.elytrabooster.common.util.GET_TOOLTIP_LINES_SIGNATURE
import mod.chloeprime.elytrabooster.common.util.findCapabilityKey
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fluids.capability.IFluidHandlerItem

/**
 * 漏油 / 漏电
 */
class EnergyLeakEnchantment : Enchantment(
    Rarity.RARE,
    ModEnchantmentCategories.BOOSTED_ELYTRAS,
    arrayOf(EquipmentSlot.CHEST)
) {
    companion object {
        private const val NAME_PLACEHOLDER = "@@ELYTRA_BOOSTER@@PLACEHOLDER_OF_LEAKING_ENCH@@"
        private val FLUID_CAP: Capability<IFluidHandlerItem> = findCapabilityKey()
        private val ENERGY_CAP: Capability<IEnergyStorage> = findCapabilityKey()
        private val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
    }

    override fun getMinCost(pLevel: Int) = 1
    override fun getMaxCost(pLevel: Int) = 9999
    override fun getMaxLevel() = 1
    override fun isTreasureOnly() = true
    override fun isCurse() = true
    override fun getDescriptionId(): String {
        val isTooltip = STACK_WALKER.walk {
            it.limit(8).anyMatch { frame -> frame.methodType == GET_TOOLTIP_LINES_SIGNATURE }
        }
        return if (isTooltip) NAME_PLACEHOLDER else super.getDescriptionId()
    }

    init {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onTooltip)
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onEnergyCost)
    }

    override fun canEnchant(stack: ItemStack): Boolean {
        return super.canEnchant(stack) && run {
            val fluid = stack.getCapability(FLUID_CAP).isPresent
            val energy = stack.getCapability(ENERGY_CAP).isPresent
            fluid || energy
        }
    }

    private fun onEnergyCost(e: ElytraCostEnergyEvent) {
        val level = EnchantmentHelper.getItemEnchantmentLevel(this, e.stack)
        if (level > 0) {
            e.amount *= (level + 1)
        }
    }

    private fun onTooltip(e: ItemTooltipEvent) {
        val enchLevel = EnchantmentHelper.getEnchantments(e.itemStack)[this] ?: 0
        if (enchLevel == 0) {
            return
        }
        val idx = e.toolTip.indexOfFirst { it is TranslatableComponent && it.key == NAME_PLACEHOLDER }
        val old = e.toolTip[idx]
        val new = getEnchantName(e.itemStack)
        new.style = old.style
        old.siblings.forEach {
            new.append(it)
        }
        e.toolTip[idx] = new
    }

    private fun getEnchantName(stack: ItemStack): MutableComponent {
        val fluid = stack.getCapability(FLUID_CAP).isPresent
        val energy = stack.getCapability(ENERGY_CAP).isPresent

        val trKey = "elytra_booster.enchantment.leak." + when {
            fluid && energy -> "both"
            fluid -> "fuel"
            energy -> "energy"
            else -> "book"
        }

        return TranslatableComponent(trKey)
    }
}