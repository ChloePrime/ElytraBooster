package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.common.enchantment.ModEnchantments.isLookingDown
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentCategory
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.common.Mod

/**
 * 减少“感受到动能”伤害（鞘翅撞击）所造成的伤害。
 * 与“天狗弹头”附魔冲突。
 *
 * @author ChloePrime
 */
@Mod.EventBusSubscriber
class ImpactProtectionEnchantment: Enchantment(
    Rarity.COMMON,
    EnchantmentCategory.ARMOR_HEAD,
    arrayOf(EquipmentSlot.HEAD)
) {
    companion object {
        private const val IMPACT_TYPE_ELYTRA = 1
        private const val IMPACT_TYPE_FALLING_OBJECT = 2

        /**
         * 鞘翅飞行时的最大摔落伤害
         * 15 ~ 5
         */
        private fun getMaxFallDamage(enchLevel: Int) = 20 - 5 * enchLevel
    }

    override fun getMinCost(level: Int) = 10 * level
    override fun getMaxCost(level: Int) = 10 * level + 30
    override fun getMaxLevel() = 3
    override fun checkCompatibility(ench: Enchantment): Boolean {
        return ench !is TenguWarheadEnchantment && super.checkCompatibility(ench)
    }

    init {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onLivingHurt)
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onLivingTick)
    }

    /**
     * 对鞘翅撞击伤害进行强效减伤，
     * 伤害将除以 (等级 + 1)
     * (/2 ~ /4)
     */
    private fun onLivingHurt(e: LivingHurtEvent) {
        if (e.entityLiving.level.isClientSide) return

        if (getImpactType(e.source) != IMPACT_TYPE_ELYTRA) {
            return
        }
        val level = EnchantmentHelper.getEnchantmentLevel(this, e.entityLiving)
        if (level == 0) return
        e.amount /= (level + 1)
    }

    private fun getImpactType(source: DamageSource): Int {
        if (source == DamageSource.FLY_INTO_WALL) {
            return IMPACT_TYPE_ELYTRA
        }
        if (source == DamageSource.FALLING_BLOCK || source == DamageSource.ANVIL) {
            return IMPACT_TYPE_FALLING_OBJECT
        }
        return 0
    }

    private val minOfMaxFallDistance = 3 + getMaxFallDamage(maxLevel)

    /**
     * 飞行时减少掉落伤害。
     */
    private fun onLivingTick(e: LivingEvent.LivingUpdateEvent) {
        if (e.entityLiving.level.isClientSide) return
        // 在下落距离小于3时不判断附魔，以减少计算量。
        if (e.entityLiving.fallDistance <= minOfMaxFallDistance) {
            return
        }
        if (!e.entityLiving.isFallFlying || e.entityLiving.isLookingDown()) {
            return
        }
        val level = EnchantmentHelper.getEnchantmentLevel(this, e.entityLiving)
        if (level == 0) return
        // 降低掉落伤害，掉落伤害不会超过上限
        val maxFallDistance = 3 + getMaxFallDamage(level)
        if (e.entityLiving.fallDistance > maxFallDistance) {
            e.entityLiving.fallDistance = maxFallDistance.toFloat()
        }
    }

    override fun getDamageProtection(level: Int, source: DamageSource): Int {
        val type = getImpactType(source)
        if (type == 0) return 0

        return when (type) {
            IMPACT_TYPE_ELYTRA -> 4 * level
            IMPACT_TYPE_FALLING_OBJECT -> 2 * level
            else -> 0
        }
    }
}
