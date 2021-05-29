package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.config.ElytraConfigEntry
import mod.chloeprime.elytrabooster.common.config.LazyFormula
import mod.chloeprime.elytrabooster.common.config.wrap
import net.minecraft.item.Item
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.ToIntFunction

/**
 * @param C ConfigEntry的类型
 */
open class BoostedElytraProperties<in C: ElytraConfigEntry>(
    var boostForce: DoubleSupplier,
    var maxEnergy: IntSupplier,
    var costFormula: ToIntFunction<IElytraInputCap>
): Item.Properties() {
    constructor() : this({ 0.0 }, { 0 }, ToIntFunction { 0 })

    fun boostForce(boostForce: DoubleSupplier): BoostedElytraProperties<C> {
        this.boostForce = boostForce
        return this
    }

    fun maxEnergy(maxEnergy: IntSupplier): BoostedElytraProperties<C> {
        this.maxEnergy = maxEnergy
        return this
    }

    fun costFormula(costFormula: ToIntFunction<IElytraInputCap>): BoostedElytraProperties<C> {
        this.costFormula = costFormula
        return this
    }

    open fun acceptConfig(config: C): BoostedElytraProperties<C> {
        boostForce { config.boostForce.get() }
        maxEnergy { config.maxFE.get() }
        costFormula = LazyFormula { config.feCost.get() }.wrap()
        return this
    }
}
