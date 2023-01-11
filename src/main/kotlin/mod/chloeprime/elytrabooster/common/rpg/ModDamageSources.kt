package mod.chloeprime.elytrabooster.common.rpg

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.world.damagesource.EntityDamageSource
import net.minecraft.world.entity.LivingEntity

object ModDamageSources {
    fun tenguWarhead(user: LivingEntity): EntityDamageSource {
        return EntityDamageSource("${ElytraBoosterMod.MODID}.tengu_warhead", user)
    }
}