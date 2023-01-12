package mod.chloeprime.elytrabooster.common.item

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi.isFlyingWithBooster
import mod.chloeprime.elytrabooster.api.common.IBoostedElytraItem
import mod.chloeprime.elytrabooster.client.ClientProxy
import mod.chloeprime.elytrabooster.common.event.ElytraCostEnergyEvent
import mod.chloeprime.elytrabooster.common.util.Aerodynamics
import mod.chloeprime.elytrabooster.common.util.StackHelper
import mod.chloeprime.elytrabooster.common.util.withLength
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.IItemRenderProperties
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.util.thread.EffectiveSide
import java.lang.StackWalker.StackFrame
import java.lang.invoke.MethodType
import java.util.*
import java.util.function.Consumer
import java.util.function.DoubleSupplier

/**
 * 无任何消耗的推进鞘翅
 * @author ChloePrime
 */
open class BoostedElytraItemBase(
    armorMaterial: ArmorMaterial,
    properties: Properties,
    private val boostForce: DoubleSupplier
) : ArmoredElytra(armorMaterial, setGroup(properties)), IBoostedElytraItem {

    companion object {
        @JvmStatic
        protected val SLOT = EquipmentSlot.CHEST
        private val ATTRIBUTE_MODIFIER_ID = UUID.fromString("391c255a-2c1f-4bd6-8ff4-9d4b36590c80")
        private val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        private val GET_TOOLTIP_LINES_SIGNATURE = MethodType.methodType(
            java.util.List::class.java,
            Player::class.java, TooltipFlag::class.java
        )

        private fun setGroup(prop: Properties): Properties {
            return prop.tab(ModCreativeTab)
        }
    }

    override fun isRepairable(stack: ItemStack) = false
    override fun isValidRepairItem(toRepair: ItemStack, repair: ItemStack) = false
    override fun canElytraFly(stack: ItemStack, entity: LivingEntity) = true
    override fun getEquipmentSlot(stack: ItemStack) = SLOT

    protected fun modifyCost(user: LivingEntity, stack: ItemStack, oldCost: Int): Int {
        val event = ElytraCostEnergyEvent(user, stack, oldCost)
        MinecraftForge.EVENT_BUS.post(event)
        return event.amount
    }

    private var lastObservedBoostForce = Double.NaN
    private var _attributes: ImmutableMultimap<Attribute, AttributeModifier>? = null
    private var _attributes2Cache: ImmutableMultimap<Attribute, AttributeModifier>? = null
    private var _ignoreAttribute = false
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
        slot: EquipmentSlot,
        stack: ItemStack
    ): Multimap<Attribute, AttributeModifier> {
        val superman = super.getAttributeModifiers(slot, stack)

        if (_ignoreAttribute) {
            return superman
        }

        val boostForceAttr = ElytraBoosterApi.Attributes.BOOST_FORCE.get()
        // 通过调用栈检测检测当前是否在生成 Tooltips 的过程中
        // at ItemStack.getTooltipLines
        // at ItemStack.getAttributeModifiers
        // at Item.getAttributeModifiers        <- this method
        val isGatheringTooltip = STACK_WALKER.walk { it.skip(2).findFirst() }.map { isGatheringTooltip(it) }.orElse(false)
        val retAttributes = if (isGatheringTooltip && EffectiveSide.get() == LogicalSide.CLIENT) {
            // 是否让 Attributes 以绿色实际值显示
            // 实现原理为将推进力伪装成攻击速度，骗原版显示为绿色
            val player = ClientProxy.localPlayer()
            if (player != null) {
                val greenMap = HashMultimap.create(attributes)
                val baseAtkSpeed = player.getAttributeBaseValue(Attributes.ATTACK_SPEED)
                val baseBoostForce = player.getAttributeBaseValue(boostForceAttr)

                attributes.entries().forEach {
                    greenMap.remove(it.key, it.value)
                }
                greenMap.put(
                    boostForceAttr,
                    AttributeModifier(
                        Item.BASE_ATTACK_SPEED_UUID,
                        "",
                        boostForce.asDouble - baseAtkSpeed + baseBoostForce,
                        AttributeModifier.Operation.ADDITION
                    )
                )
                greenMap
            } else {
                attributes
            }
        } else {
            attributes
        }

        return if (slot == this.slot) {
            // 盔甲值 + 推进力
            ImmutableMultimap.builder<Attribute, AttributeModifier>()
                .putAll(retAttributes)
                .putAll(superman)
                .build()
        } else {
            superman
        }
    }

    private fun isGatheringTooltip(stackframe: StackFrame): Boolean {
        return stackframe.declaringClass == ItemStack::class.java &&
                stackframe.methodType == StackHelper.GET_TOOLTIP_LINES_SIGNATURE
    }

    private fun getTotalMultiplierForPlayer(player: Player, attribute: Attribute): Double {
        return try {
            _ignoreAttribute = true
            Optional.ofNullable(player.getAttribute(attribute)).stream()
                .mapToDouble { attr ->
                    attr.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL).sumOf { it.amount }
                }
                .dropWhile { it == 0.0 }
                .findAny()
                .orElse(1.0)
        } finally {
            _ignoreAttribute = false
        }
    }

    override fun initializeClient(consumer: Consumer<IItemRenderProperties>) {
        super.initializeClient(consumer)
        consumer.accept(ClientProxy.elytraBaseRp())
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

        val motion: Vec3 = e.player.deltaMovement
        val lookDir = e.player.lookAngle
        // kv^2
        val airResistance = Aerodynamics.AIR_DRAG * motion.lengthSqr()
        val airResistanceVec = motion.withLength(airResistance)
        // a=F/m=cF，c为常数
        e.player.deltaMovement = motion.add(
            a0 * lookDir.x - airResistanceVec.x,
            a0 * lookDir.y - airResistanceVec.y,
            a0 * lookDir.z - airResistanceVec.z
        )
    }
}
