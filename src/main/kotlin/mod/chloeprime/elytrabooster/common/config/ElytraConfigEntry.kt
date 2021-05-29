package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val MAX_FE_COMMENT = "Max FE Stored"
private val FE_COST_COMMENT = arrayOf(
    "FE Cost Formula ",
    "'x' means input value of turning ",
    "and 'y' means input value of forward boost."
)
private const val BOOST_FORCE_COMMENT = "Propulsive Force"

private const val MAX_FE_KEY = "maxEnergy"
private const val FE_COST_KEY = "energyCostFormula"
private const val BOOST_FORCE_KEY = "boostForce"

open class ElytraConfigEntry protected constructor(
    val maxFE: ForgeConfigSpec.IntValue,
    val feCost: ForgeConfigSpec.ConfigValue<String>,
    val boostForce: ForgeConfigSpec.DoubleValue
) {
    companion object {

        @JvmStatic
        protected fun genMaxFE(
            builder: ForgeConfigSpec.Builder,
            defaultValues: ElytraConfigDefaultValues
        ): ForgeConfigSpec.IntValue = builder
            .comment(MAX_FE_COMMENT)
            .defineInRange(MAX_FE_KEY, defaultValues.maxFuel, 0, Int.MAX_VALUE)

        @JvmStatic
        protected fun genFeCost(
            builder: ForgeConfigSpec.Builder,
            defaultValues: ElytraConfigDefaultValues
        ): ForgeConfigSpec.ConfigValue<String> = builder
            .comment(*FE_COST_COMMENT)
            .define(FE_COST_KEY, defaultValues.fuelCost)

        @JvmStatic
        protected fun genBoostForce(
            builder: ForgeConfigSpec.Builder,
            defaultValues: ElytraConfigDefaultValues
        ): ForgeConfigSpec.DoubleValue = builder
            .comment(BOOST_FORCE_COMMENT)
            .defineInRange(BOOST_FORCE_KEY, defaultValues.boostForce, 0.0, Double.MAX_VALUE)
    }
}

open class ElytraConfigDefaultValues(
    var maxFuel: Int = 0,
    var fuelCost: String = "",
    var boostForce: Double = 0.0
)