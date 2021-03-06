package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val CHARGE_SPEED_COMMENT = "Max FE Charge Speed"

private const val CHARGE_SPEED_KEY = "chargeSpeed"

class FeElytraConfigEntry private constructor(
    maxFE: ForgeConfigSpec.IntValue,
    val chargeSpeed: ForgeConfigSpec.IntValue,
    feCost: ForgeConfigSpec.ConfigValue<String>,
    boostForce: ForgeConfigSpec.DoubleValue,
) : ElytraConfigEntry(maxFE, feCost, boostForce) {
    companion object {
        fun create(
            builder: ForgeConfigSpec.Builder,
            tier: Int,
            defaultValues: FeElytraConfigDefaultValues
        ): FeElytraConfigEntry {
            builder.comment("T$tier Electric Elytra Settings").push("fe_t$tier")
            val maxFE = genMaxFE(builder, defaultValues)
            val feCost = genFeCost(builder, defaultValues)
            val boostForce = genBoostForce(builder, defaultValues)
            val chargeSpeed = genChargeSpeed(builder, defaultValues)
            builder.pop()
            return FeElytraConfigEntry(maxFE, chargeSpeed, feCost, boostForce)
        }

        private fun genChargeSpeed(
            builder: ForgeConfigSpec.Builder,
            defaultValues: FeElytraConfigDefaultValues
        ): ForgeConfigSpec.IntValue = builder
            .comment(CHARGE_SPEED_COMMENT)
            .defineInRange(CHARGE_SPEED_KEY, defaultValues.chargeSpeed, 0, Int.MAX_VALUE)
    }
}

class FeElytraConfigDefaultValues(
    maxFE: Int = 0,
    feCost: String = "",
    boostForce: Double = 0.0,
    var chargeSpeed: Int = 0,
) : ElytraConfigDefaultValues(maxFE, feCost, boostForce)