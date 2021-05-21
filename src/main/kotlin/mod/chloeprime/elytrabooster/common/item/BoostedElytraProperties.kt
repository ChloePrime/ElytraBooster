package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.item.Item
import java.util.function.ToIntFunction

class BoostedElytraProperties(
    var boostForce: Double,
    var maxEnergy: Int,
    var costFormula: ToIntFunction<IElytraInputCap>
): Item.Properties() {
    constructor() : this(0.0, 0, ToIntFunction { 0 })

    fun boostForce(boostForce: Double): BoostedElytraProperties {
        this.boostForce = boostForce
        return this
    }

    fun maxEnergy(maxEnergy: Int): BoostedElytraProperties {
        this.maxEnergy = maxEnergy
        return this
    }

    fun costFormula(costFormula: ToIntFunction<IElytraInputCap>): BoostedElytraProperties {
        this.costFormula = costFormula
        return this
    }

}
