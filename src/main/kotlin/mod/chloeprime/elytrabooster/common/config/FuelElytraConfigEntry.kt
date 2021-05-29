package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val CHARGE_SPEED_COMMENT = "Max FE Charge Speed"

private const val CHARGE_SPEED_KEY = "chargeSpeed"

class FuelElytraConfigEntry private constructor(
    maxFE: ForgeConfigSpec.IntValue,
    feCost: ForgeConfigSpec.ConfigValue<String>,
    boostForce: ForgeConfigSpec.DoubleValue,
) : ElytraConfigEntry(maxFE, feCost, boostForce) {
    companion object {
        fun create(
            builder: ForgeConfigSpec.Builder,
            tier: Int,
            defaultValues: FuelElytraConfigDefaultValues
        ): FuelElytraConfigEntry {
            builder.comment("T$tier Fuel Elytra Settings").push("fuel_t$tier")
            val maxFE = genMaxFE(builder, defaultValues)
            val feCost = genFeCost(builder, defaultValues)
            val boostForce = genBoostForce(builder, defaultValues)
            builder.pop()
            return FuelElytraConfigEntry(maxFE, feCost, boostForce)
        }
    }
}

class FuelElytraConfigDefaultValues(
    maxFE: Int = 0,
    feCost: String = "",
    boostForce: Double = 0.0,
) : ElytraConfigDefaultValues(maxFE, feCost, boostForce)