package mod.chloeprime.elytrabooster.common.item

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isFlyingWithBooster
import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.common.util.Aerodynamics
import mod.chloeprime.elytrabooster.common.util.withLength
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.Attribute
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.item.IArmorMaterial
import net.minecraft.item.ItemStack
import net.minecraft.util.math.vector.Vector3d
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.*
import java.util.function.DoubleSupplier

/**
 * 无任何消耗的推进鞘翅
 * @author ChloePrime
 */
open class BoostedElytraItemBase(
    armorMaterial: IArmorMaterial,
    properties: Properties,
    private val boostForce: DoubleSupplier
) : ArmoredElytra(armorMaterial, setGroup(properties)), IBoostedElytraItem {

    companion object {
        @JvmStatic
        protected val SLOT = EquipmentSlotType.CHEST
        private val ATTRIBUTE_MODIFIER_ID = UUID.fromString("391c255a-2c1f-4bd6-8ff4-9d4b36590c80")

        private fun setGroup(prop: Properties): Properties {
            return prop.group(ModItemGroup)
        }
    }

    override fun isRepairable(stack: ItemStack) = false
    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack) = false
    override fun canElytraFly(stack: ItemStack, entity: LivingEntity) = true
    override fun getEquipmentSlot(stack: ItemStack) = SLOT

    private var lastObservedBoostForce = Double.NaN
    private var _attributes: ImmutableMultimap<Attribute, AttributeModifier>? = null
    private val attributes
        get(): ImmutableMultimap<Attribute, AttributeModifier> {
            val newBoostForce = boostForce.asDouble
            if (newBoostForce.isNaN()) {
                throw IllegalArgumentException("Boost force of $registryName is NaN")
            }
            if (newBoostForce == lastObservedBoostForce) {
                return _attributes!!
            }
            lastObservedBoostForce = newBoostForce
            _attributes = ImmutableMultimap.of(
                ElytraBoosterApi.Attributes.BOOST_FORCE.get(),
                AttributeModifier(
                    ATTRIBUTE_MODIFIER_ID,
                    "Boosted Elytra Boost Force",
                    newBoostForce,
                    AttributeModifier.Operation.ADDITION
                )
            )
            return _attributes!!
        }

    /**
     * 将推力属性与父类提供的盔甲值合并。
     */
    override fun getAttributeModifiers(
        slot: EquipmentSlotType,
        stack: ItemStack
    ): Multimap<Attribute, AttributeModifier> {
        val superman = super.getAttributeModifiers(slot, stack)
        return if (slot == this.slot) {
            // 盔甲值 + 推进力
            ImmutableMultimap.builder<Attribute, AttributeModifier>()
                .putAll(attributes)
                .putAll(superman)
                .build()
        } else {
            superman
        }
    }
}

/**
 * 让推进鞘翅真正附加推力
 * 加速度公式 a = a0 * lookVec - kv^2 * e(motion)
 * @author ChloePrime
 */
@Mod.EventBusSubscriber
internal object ElytraBoostingEventHandler {
    /**
     * 双端执行
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
        if (e.phase === TickEvent.Phase.END) return
        if (!e.player.isFlyingWithBooster) return

        val input = ElytraBoosterApi.getElytraInputOrNull(e.player) ?: return
        // 空气阻力公式 F=0.5CρSv^2=cv^2 c为常数，v为速度

        // v = √a0 / √k
        // a0 = v^2 * k * 加速度方向
        val a0 = Aerodynamics.getAccelerationForPlayer(e.player) * input.moveForward

        val motion: Vector3d = e.player.motion
        val lookDir = e.player.lookVec
        // kv^2
        val airResistance = Aerodynamics.AIR_DRAG * motion.lengthSquared()
        val airResistanceVec = motion.withLength(airResistance)
        // a=F/m=cF，c为常数
        e.player.motion = motion.add(
            a0 * lookDir.x - airResistanceVec.x,
            a0 * lookDir.y - airResistanceVec.y,
            a0 * lookDir.z - airResistanceVec.z
        )
    }
}
