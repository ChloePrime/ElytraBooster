package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.item.Item
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.ToIntFunction

open class BoostedElytraProperties(
    var boostForce: DoubleSupplier,
    var maxEnergy: IntSupplier,
    var costFormula: ToIntFunction<IElytraInputCap>
): Item.Properties() {
    constructor() : this({ 0.0 }, { 0 }, ToIntFunction { 0 })

    fun boostForce(boostForce: DoubleSupplier): BoostedElytraProperties {
        this.boostForce = boostForce
        return this
    }

    fun maxEnergy(maxEnergy: IntSupplier): BoostedElytraProperties {
        this.maxEnergy = maxEnergy
        return this
    }

    fun costFormula(costFormula: ToIntFunction<IElytraInputCap>): BoostedElytraProperties {
        this.costFormula = costFormula
        return this
    }
}
