package mod.chloeprime.elytrabooster.common.config

import net.minecraftforge.common.ForgeConfigSpec

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

    val FE_T1: FeElytraConfigEntry

    /* T2 电动鞘翅 */

    val FE_T2: FeElytraConfigEntry

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

        FE_T1 = FeElytraConfigEntry.create(builder, 1, FeElytraConfigDefaultValues().apply {
            maxFE = 50000
            chargeSpeed = 100
            feCost = "10*x+50*y+2"
            boostForce = 1.5
        })

        /* 电动 T2 */

        FE_T2 = FeElytraConfigEntry.create(builder, 2, FeElytraConfigDefaultValues().apply {
            maxFE = 1250000
            chargeSpeed = 1000
            feCost = "50*x+250*y+10"
            boostForce = 2.0
        })

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