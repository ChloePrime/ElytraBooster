package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val MAX_FE_COMMENT = "Max FE Stored"
private const val CHARGE_SPEED_COMMENT = "Max FE Charge Speed"
private val FE_COST_COMMENT = arrayOf(
    "FE Cost Formula ",
    "'x' means input value of turning ",
    "and 'y' means input value of forward boost."
)
private const val BOOST_FORCE_COMMENT = "Propulsive Force"

private const val MAX_FE_KEY = "maxEnergy"
private const val CHARGE_SPEED_KEY = "chargeSpeed"
private const val FE_COST_KEY = "energyCostFormula"
private const val BOOST_FORCE_KEY = "boostForce"

class FeElytraConfigEntry private constructor(
    val maxFE: ForgeConfigSpec.IntValue,
    val chargeSpeed: ForgeConfigSpec.IntValue,
    val feCost: ForgeConfigSpec.ConfigValue<String>,
    val boostForce: ForgeConfigSpec.DoubleValue
) {
    companion object {
        fun create(
            builder: ForgeConfigSpec.Builder,
            tier: Int,
            defaultValues: FeElytraConfigDefaultValues
        ): FeElytraConfigEntry {
            builder.comment("T$tier Electric Elytra Settings").push("fe_t$tier")
            val maxFE = builder
                .comment(MAX_FE_COMMENT)
                .defineInRange(MAX_FE_KEY, defaultValues.maxFE, 0, Int.MAX_VALUE)
            val chargeSpeed = builder
                .comment(CHARGE_SPEED_COMMENT)
                .defineInRange(CHARGE_SPEED_KEY, defaultValues.chargeSpeed, 0, Int.MAX_VALUE)
            val feCost = builder
                .comment(*FE_COST_COMMENT)
                .define(FE_COST_KEY, defaultValues.feCost)
            val boostForce = builder
                .comment(BOOST_FORCE_COMMENT)
                .defineInRange(BOOST_FORCE_KEY, defaultValues.boostForce, 0.0, Double.MAX_VALUE)
            builder.pop()
            return FeElytraConfigEntry(maxFE, chargeSpeed, feCost, boostForce)
        }
    }
}

class FeElytraConfigDefaultValues(
    var maxFE: Int = 0,
    var chargeSpeed: Int = 0,
    var feCost: String = "",
    var boostForce: Double = 0.0
)