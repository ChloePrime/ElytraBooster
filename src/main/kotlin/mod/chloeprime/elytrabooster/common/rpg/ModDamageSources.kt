package mod.chloeprime.elytrabooster.common.rpg

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.util.DamageSource

object ModDamageSources {
    val TENGU_WARHEAD =
        DamageSource("${ElytraBoosterMod.MODID}.tengu_warhead")
        .setDamageBypassesArmor()
}