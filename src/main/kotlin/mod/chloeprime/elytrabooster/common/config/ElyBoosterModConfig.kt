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

    /* 不同的鞘翅 */

    val FUEL_T1: FuelElytraConfigEntry
    val FE_T1: FeElytraConfigEntry
    val FE_T2: FeElytraConfigEntry
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
            maxFuel = 250000
            chargeSpeed = 500
            fuelCost = "50*x+250*y+10"
            boostForce = 1.5
        })

        /* 电动 T2 */

        FE_T2 = FeElytraConfigEntry.create(builder, 2, FeElytraConfigDefaultValues().apply {
            maxFuel = 6250000
            chargeSpeed = 5000
            fuelCost = "250*x+1250*y+50"
            boostForce = 2.0
        })

        FUEL_T1 = FuelElytraConfigEntry.create(builder, 1, FuelElytraConfigDefaultValues().apply {
            maxFuel = 5000
            fuelCost = "1*x+3*y+1"
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