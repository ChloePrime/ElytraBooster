package mod.chloeprime.elytrabooster.api.common

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.Attribute
import net.minecraft.entity.ai.attributes.RangedAttribute
import net.minecraft.inventory.EquipmentSlotType
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister

/**
 * @author ChloePrime
 */
object ElytraBoosterApi {
    const val MODID = ElytraBoosterMod.MODID

    object Attributes {
        @JvmField
        val REGISTRY = DeferredRegister.create(Attribute::class.java, MODID)!!

        @JvmField
        val BOOST_FORCE: RegistryObject<Attribute> = REGISTRY.register("boost_force") {
            RangedAttribute(
                "$MODID.boost_force",
                0.0, 0.0, Double.MAX_VALUE
            ).apply {
                shouldWatch = true
            }
        }
    }

    /**
     * 判断一个实体是否装备了可推进的鞘翅
     */
    @JvmStatic
    val LivingEntity.isEquippingBoostedElytra: Boolean
        get() {
            return getItemStackFromSlot(EquipmentSlotType.CHEST).item is IBoostedElytraItem
        }

    /**
     * 判断一个实体是否处于飞行并穿戴了可推进鞘翅
     */
    @JvmStatic
    val LivingEntity.isFlyingWithBooster: Boolean
        get() {
            return isElytraFlying && isEquippingBoostedElytra
        }

    /**
     * 强制获取推进鞘翅输入状态 （ELYTRA_INPUT 能力附加的对象），
     * 如果CapToken或附加的对象不存在则直接报错
     */
    @JvmStatic
    fun getElytraInput(provider: ICapabilityProvider): IElytraInputCap {
        return provider.getCapability(ElytraBoosterCapabilities.ELYTRA_INPUT!!).orElseThrow {
            IllegalStateException("Accessing caps before init")
        }
    }

    /**
     * 获取推进鞘翅输入状态 （ELYTRA_INPUT 附加的对象）
     * 如果CapToken或附加的对象不存在则返回null
     */
    @JvmStatic
    fun getElytraInputOrNull(provider: ICapabilityProvider): IElytraInputCap? {
        return ElytraBoosterCapabilities.ELYTRA_INPUT?.let {
            val optional = provider.getCapability(it)
            return if (optional.isPresent) optional.resolve().get() else null
        }
    }
}
