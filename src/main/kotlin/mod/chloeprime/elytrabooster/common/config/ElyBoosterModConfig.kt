package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val MAX_FE_COMMENT = "Max FE Stored"
private const val FE_COST_COMMENT = "FE Cost Formula"
private const val DURABILITY_COMMENT = "Max Durability"
private const val BOOST_FORCE_COMMENT = "Propulsive Force"

private const val MAX_FE_KEY = "max_fe"
private const val FE_COST_KEY = "fe_formula"
private const val DURABILITY_KEY = "durability"
private const val BOOST_FORCE_KEY = "boost_force"

object ElyBoosterModConfig {

    val CONFIG: ForgeConfigSpec

    /* T1 电动鞘翅 */

    val T1_MAX_FE: ForgeConfigSpec.IntValue
    val T1_FE_COST: ForgeConfigSpec.ConfigValue<String>
    val T1_DURABILITY: ForgeConfigSpec.IntValue
    val T1_BOOST_FORCE: ForgeConfigSpec.DoubleValue

    /* 创造推进鞘翅 */

    val CREATIVE_BOOST_POWER: ForgeConfigSpec.DoubleValue

    init {
        val builder = ForgeConfigSpec.Builder()

        builder.comment("T1 Electric Elytra Settings").push("fe_t1")
        T1_MAX_FE = builder.comment(MAX_FE_COMMENT).defineInRange(
            MAX_FE_KEY, 50000, 0, Int.MAX_VALUE
        )
        T1_FE_COST = builder.comment(FE_COST_COMMENT).define(
            FE_COST_KEY, "10*x+50*y-2"
        )
        T1_DURABILITY = builder.comment(DURABILITY_COMMENT).defineInRange(
            DURABILITY_KEY, 432, 0, Int.MAX_VALUE
        )
        T1_BOOST_FORCE = builder.comment(BOOST_FORCE_COMMENT).defineInRange(
            BOOST_FORCE_KEY, 1.5, 0.0, Double.MAX_VALUE
        )
        builder.pop()

        builder.comment("Creative Boosted Elytra Settings").push("creative")
        CREATIVE_BOOST_POWER = builder.defineInRange(
            BOOST_FORCE_KEY, 4.0, 0.0, Double.MAX_VALUE
        )
        builder.pop()

        CONFIG = builder.build()
    }

}