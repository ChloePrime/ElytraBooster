package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.util.fastLength
import net.minecraft.entity.LivingEntity
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import kotlin.math.acos

object ModEnchantments {
    val REGISTRY = DeferredRegister.create(
        ForgeRegistries.ENCHANTMENTS, ElytraBoosterMod.MODID
    )!!

    val IMPACT_PROTECTION = REGISTRY.register("impact_protection") {
        ImpactProtectionEnchantment()
    }

    val TENGU_WARHEAD = REGISTRY.register("tengu_warhead") {
        TenguWarheadEnchantment()
    }

    internal fun LivingEntity.isLookingDown(): Boolean {
        val headVec = this.lookVec
        // 被判定为鞘翅撞击时，视线方向与-y轴的最小夹角（45°）
        val minAngle = Math.PI / 4
        // 求向量与-y轴的夹角，并判断是否处于头朝下。
        return acos(-headVec.y / headVec.fastLength()) <= minAngle
    }
}