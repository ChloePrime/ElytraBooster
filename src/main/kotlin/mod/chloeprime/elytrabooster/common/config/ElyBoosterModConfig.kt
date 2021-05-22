package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

private const val MAX_FE_COMMENT = "Max FE Stored"
private const val FE_COST_COMMENT = "FE Cost Formula ('x' means input value of turning and 'y' means input value of forward boost.)"
private const val DURABILITY_COMMENT = "Max Durability"
private const val BOOST_FORCE_COMMENT = "Propulsive Force"

private const val MAX_FE_KEY = "maxEnergy"
private const val FE_COST_KEY = "energyCostFormula"
private const val DURABILITY_KEY = "durability"
private const val BOOST_FORCE_KEY = "boostForce"

/**
 * 警告：耐久度上限的配置暂时无法生效
 * @author ChloePrime
 */
object ElyBoosterModConfig {

    val CONFIG: ForgeConfigSpec

    /* 空气动力学参数 */

    val AIR_DRAG_SCALE: ForgeConfigSpec.DoubleValue

    /* T1 电动鞘翅 */

    val FE_T1_MAX_FE: ForgeConfigSpec.IntValue
    val FE_T1_FE_COST: ForgeConfigSpec.ConfigValue<String>
    val FE_T1_DURABILITY: ForgeConfigSpec.IntValue
    val FE_T1_BOOST_FORCE: ForgeConfigSpec.DoubleValue

    /* T2 电动鞘翅 */

    val FE_T2_MAX_FE: ForgeConfigSpec.IntValue
    val FE_T2_FE_COST: ForgeConfigSpec.ConfigValue<String>
    val FE_T2_DURABILITY: ForgeConfigSpec.IntValue
    val FE_T2_BOOST_FORCE: ForgeConfigSpec.DoubleValue

    /* 创造推进鞘翅 */

    val CREATIVE_BOOST_POWER: ForgeConfigSpec.DoubleValue

    /* 杂项 */

    val BAN_FIREWORK_BOOST: ForgeConfigSpec.BooleanValue

    init {
        val builder = ForgeConfigSpec.Builder()

        builder.comment("Aerodynamics Constants").push("aerodynamics")
        AIR_DRAG_SCALE = builder
            .comment(
                "Air Drag Factor (Scale against base value)",
                "Note that boost acceleration is multiplied by this value.",
                "So, the larger this value is, ",
                "the easier it is to fly.",
                "",
                "Modifying this value is NOT SUGGESTED unless you know how the source code works."
            ).defineInRange("airDrag", 1.0, 0.0, Double.MAX_VALUE)
        builder.pop()

        /* 电动 T1 */

        builder.comment("T1 Electric Elytra Settings").push("fe_t1")
        FE_T1_MAX_FE = builder
            .comment(MAX_FE_COMMENT)
            .defineInRange(MAX_FE_KEY, 50000, 0, Int.MAX_VALUE)
        FE_T1_FE_COST = builder
            .comment(FE_COST_COMMENT)
            .define(FE_COST_KEY, "10*x+50*y+2")
        FE_T1_DURABILITY = builder
            .comment(DURABILITY_COMMENT)
            .defineInRange(DURABILITY_KEY, 432, 0, Int.MAX_VALUE)
        FE_T1_BOOST_FORCE = builder
            .comment(BOOST_FORCE_COMMENT)
            .defineInRange(BOOST_FORCE_KEY, 1.5, 0.0, Double.MAX_VALUE)
        builder.pop()

        /* 电动 T2 */

        builder.comment("T2 Electric Elytra Settings").push("fe_t2")
        FE_T2_MAX_FE = builder
            .comment(MAX_FE_COMMENT)
            .defineInRange(MAX_FE_KEY, 1250000, 0, Int.MAX_VALUE)
        FE_T2_FE_COST = builder
            .comment(FE_COST_COMMENT)
            .define(FE_COST_KEY, "50*x+250*y+10")
        FE_T2_DURABILITY = builder
            .comment(DURABILITY_COMMENT)
            .defineInRange(DURABILITY_KEY, 2550, 0, Int.MAX_VALUE)
        FE_T2_BOOST_FORCE = builder
            .comment(BOOST_FORCE_COMMENT)
            .defineInRange(BOOST_FORCE_KEY, 2.0, 0.0, Double.MAX_VALUE)
        builder.pop()

        /* 创造 */

        builder.comment("Creative Boosted Elytra Settings").push("creative")
        CREATIVE_BOOST_POWER = builder
            .defineInRange(BOOST_FORCE_KEY, 4.0, 0.0, Double.MAX_VALUE)
        builder.pop()

        /* 杂项 */

        builder.comment("Misc").push("misc")
        BAN_FIREWORK_BOOST = builder
            .comment("Prevent vanilla firework from boosting elytra flight")
            .define("no_firework_boost", true)
        builder.pop()

        CONFIG = builder.build()
    }
}