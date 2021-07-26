package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.common.rpg.ModDamageSources
import mod.chloeprime.elytrabooster.common.util.fastLength
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentType
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.util.DamageSource
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.world.Explosion
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.EventPriority

/**
 * "天狗弹头"附魔
 * 撞击！爆炸！
 *
 * @author ChloePrime
 */
class TenguWarheadEnchantment: Enchantment(
    Rarity.VERY_RARE,
    EnchantmentType.ARMOR_HEAD,
    arrayOf(EquipmentSlotType.HEAD)
) {
    override fun getMinEnchantability(enchantmentLevel: Int) = 20
    override fun getMaxEnchantability(enchantmentLevel: Int) = 100
    override fun getMaxLevel() = 1
    override fun isTreasureEnchantment() = true
    override fun canApplyTogether(ench: Enchantment): Boolean {
        return ench !is ImpactProtectionEnchantment && super.canApplyTogether(ench)
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
        if (entity.world.isRemote || e.source != DamageSource.FLY_INTO_WALL) {
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
        EnchantmentHelper.getMaxEnchantmentLevel(this@TenguWarheadEnchantment, this)

    private fun onLivingHurt(e: LivingHurtEvent) {
        val entity = e.entityLiving
        if (entity.world.isRemote || e.source != DamageSource.FLY_INTO_WALL) {
            return
        }
        if (entity.getEnchLevel() == 0) return

        // 计算速度是否达标，并造成伤害
        val minSpeed = 0.2
        val speed = entity.motion.fastLength().toFloat()
        if (speed < minSpeed) return
        causeWarheadDamage(entity, speed)

        // 爆炸特效/方块破坏效果
        val explosionPower = 2 * speed + 1
        entity.world.createExplosion(
            entity, ModDamageSources.TENGU_WARHEAD, null,
            entity.posX, entity.posY, entity.posZ,
            explosionPower, false, Explosion.Mode.BREAK
        )
    }

    private fun causeWarheadDamage(self: LivingEntity, speed: Float) {
        val power = (20 + 30 / MathHelper.fastInvSqrt(speed.toDouble()))
        val roughBB = AxisAlignedBB(-power, -power, -power, power, power, power).offset(self.positionVec)

        for (target in self.entity.world.getEntitiesWithinAABB(LivingEntity::class.java, roughBB)) {
            // 不会伤害自己
            if (target === self) continue

            val distance = target.positionVec.subtract(self.positionVec).fastLength()
            if (distance > power) {
                continue
            }
            // 伤害 = (√power - √distance) ^ 2
            val sqrtPower = 1 / MathHelper.fastInvSqrt(power).toFloat()
            val sqrtDist = 1 / MathHelper.fastInvSqrt(distance).toFloat()
            val sqrtDmg = sqrtPower - sqrtDist
            target.attackEntityFrom(
                ModDamageSources.TENGU_WARHEAD, sqrtDmg * sqrtDmg
            )
        }
    }
}