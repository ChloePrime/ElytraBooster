package mod.chloeprime.elytrabooster.common.enchantment

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.item.ArmoredElytra
import mod.chloeprime.elytrabooster.common.item.BoostedElytraItemBase
import mod.chloeprime.elytrabooster.common.util.fastLength
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentCategory
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

    val FLY_DODGING = REGISTRY.register("fly_dodging") {
        FlyDodgingEnchantment()
    }

    val ENERGY_LEAK = REGISTRY.register("leaking") {
        EnergyLeakEnchantment()
    }

    val TENGU_WARHEAD = REGISTRY.register("tengu_warhead") {
        TenguWarheadEnchantment()
    }

    internal fun LivingEntity.isLookingDown(): Boolean {
        val headVec = this.lookAngle
        // 被判定为鞘翅撞击时，视线方向与-y轴的最小夹角（45°）
        val minAngle = Math.PI / 4
        // 求向量与-y轴的夹角，并判断是否处于头朝下。
        return acos(-headVec.y / headVec.fastLength()) <= minAngle
    }
}

object ModEnchantmentCategories {

    val ELYTRAS = EnchantmentCategory.create("${ElytraBoosterMod.MODID.uppercase()}__ELYTRAS") {item ->
        item == Items.ELYTRA || item is ArmoredElytra || item.defaultInstance.`is`(ELYTRA_TAG)
    }
    val BOOSTED_ELYTRAS = EnchantmentCategory.create("${ElytraBoosterMod.MODID.uppercase()}__BOOSTED_ELYTRAS") {item ->
        item is BoostedElytraItemBase
    }

    private val ELYTRA_TAG = ItemTags.create(ResourceLocation("forge", "elytras"))
}