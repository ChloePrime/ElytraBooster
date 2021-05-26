package mod.chloeprime.elytrabooster.common.item

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.common.config.ElyBoosterModConfig
import mod.chloeprime.elytrabooster.common.config.FeElytraConfigEntry
import mod.chloeprime.elytrabooster.common.config.LazyFormula
import mod.chloeprime.elytrabooster.common.config.wrap
import net.minecraft.item.Item
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.ToIntFunction

open class BoostedElytraProperties(
    var boostForce: DoubleSupplier,
    var maxEnergy: IntSupplier,
    var chargeSpeed: IntSupplier,
    var costFormula: ToIntFunction<IElytraInputCap>
): Item.Properties() {
    constructor() : this({ 0.0 }, { 0 }, { 0 }, ToIntFunction { 0 })

    fun boostForce(boostForce: DoubleSupplier): BoostedElytraProperties {
        this.boostForce = boostForce
        return this
    }

    fun maxEnergy(maxEnergy: IntSupplier): BoostedElytraProperties {
        this.maxEnergy = maxEnergy
        return this
    }

    fun chargeSpeed(chargeSpeed: IntSupplier): BoostedElytraProperties {
        this.chargeSpeed = chargeSpeed
        return this
    }

    fun costFormula(costFormula: ToIntFunction<IElytraInputCap>): BoostedElytraProperties {
        this.costFormula = costFormula
        return this
    }

    fun acceptConfig(config: FeElytraConfigEntry): BoostedElytraProperties {
        boostForce { config.boostForce.get() }
        maxEnergy { config.maxFE.get() }
        chargeSpeed { config.chargeSpeed.get() }
        costFormula = LazyFormula { config.feCost.get() }.wrap()
        maxDamage(config.durability.get())
        return this
    }
}
