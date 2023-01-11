package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.common.rpg.ModDamageSources
import mod.chloeprime.elytrabooster.common.util.fastLength
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentCategory
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Explosion
import net.minecraft.world.phys.AABB
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.EventPriority

/**
 * "天狗弹头"附魔，
 * 撞击！爆炸！
 * 附带摔落保护效果，以减少撞击后摔死的几率。
 *
 * @author ChloePrime
 */
class TenguWarheadEnchantment: Enchantment(
    Rarity.VERY_RARE,
    EnchantmentCategory.ARMOR_HEAD,
    arrayOf(EquipmentSlot.HEAD)
) {
    override fun getMinCost(enchantmentLevel: Int) = 20
    override fun getMaxCost(enchantmentLevel: Int) = 100
    override fun getMaxLevel() = 1
    override fun isTreasureOnly() = true
    override fun checkCompatibility(ench: Enchantment): Boolean {
        return ench !is ImpactProtectionEnchantment && super.checkCompatibility(ench)
    }

    /**
     * 带摔落保护效果。
     * 原版摔落保护 4 返回 12
     * 此处返回 10
     */
    override fun getDamageProtection(level: Int, source: DamageSource): Int {
        return if (source == DamageSource.FALL) {
            10
        } else {
            super.getDamageProtection(level, source)
        }
    }

    init {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onLivingHurt)
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onLivingDamage)
    }

    /**
     * 如果撞击前HP比例大于某一定值，
     * 则在撞击致命伤后保留1点HP
     */
    private fun onLivingDamage(e: LivingDamageEvent) {
        val entity = e.entityLiving
        if (entity.level.isClientSide || e.source != DamageSource.FLY_INTO_WALL) {
            return
        }
        if (entity.getEnchLevel() == 0) return

        tryProtectFatalDamage(e)
    }

    private fun tryProtectFatalDamage(e: LivingDamageEvent) {
        val entity = e.entityLiving
        val isFatalDamage = e.amount >= entity.health
        val minHpRate = 0.25F

        if (isFatalDamage && entity.health / entity.maxHealth >= minHpRate) {
            e.amount = entity.health - 1
        }
    }

    private fun LivingEntity.getEnchLevel() =
        EnchantmentHelper.getEnchantmentLevel(this@TenguWarheadEnchantment, this)

    private fun onLivingHurt(e: LivingHurtEvent) {
        val user = e.entityLiving
        if (user.level.isClientSide || e.source != DamageSource.FLY_INTO_WALL) {
            return
        }
        if (user.getEnchLevel() == 0) return

        // 计算速度是否达标，并造成伤害
        val minSpeed = 0.2
        val speed = user.deltaMovement.fastLength().toFloat()
        if (speed < minSpeed) return
        causeWarheadDamage(user, speed)

        // 爆炸特效/方块破坏效果
        val explosionPower = 2 * speed + 1
        user.level.explode(
            user, ModDamageSources.tenguWarhead(user), null,
            user.x, user.y, user.z,
            explosionPower, false, Explosion.BlockInteraction.BREAK
        )

        // 停止飞行状态
        user.tryCancelFlight()
    }

    private fun causeWarheadDamage(user: LivingEntity, speed: Float) {
        /**
         * 攻击力 / 射程
         */
        val rangeScale = 5
        val power = (20 + 30 / Mth.fastInvSqrt(speed.toDouble()))
        val range = power / rangeScale
        val roughBB = AABB(-range, -range, -range, range, range, range).move(user.position())

        for (target in user.level.getEntitiesOfClass(LivingEntity::class.java, roughBB)) {
            // 不会伤害自己
            if (target === user) continue

            val distance = target.position().subtract(user.position()).fastLength()
            if (distance > range) {
                continue
            }
            // 伤害 = √(power ^ 2 - (distance * scale) ^ 2)
            // 伤害随距离从 power 衰减到 0
            val damageAmount = 1 / Mth.fastInvSqrt(
                power * power - rangeScale * rangeScale * distance * distance
            ).toFloat()

            target.hurt(
                ModDamageSources.tenguWarhead(user), damageAmount
            )
        }
    }

    private fun LivingEntity.tryCancelFlight() {
        fallDistance = 0F
        isOnGround = true
    }
}