package mod.chloeprime.elytrabooster.common.caps

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterCapabilities
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

/**
 * 用于存储可推进鞘翅的输入信息
 */
class ElytraInputCap(
    override var moveStrafe: Float,
    override var moveForward: Float
) : IElytraInputCap

class Provider(
    private val entity: LivingEntity
): ICapabilityProvider {
    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return ElytraBoosterCapabilities.ELYTRA_INPUT.orEmpty(cap, LazyOptional.of { capInstance })
    }

    private val capInstance by lazy {
        ElytraInputCap(0F, 0F)
    }
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
internal object Registering {
    @SubscribeEvent
    fun onRegisterCaps(e: RegisterCapabilitiesEvent) {
        e.register(IElytraInputCap::class.java)
    }
}

@Mod.EventBusSubscriber
internal object Attaching {
    @SubscribeEvent
    fun onAttachCapability(e: AttachCapabilitiesEvent<Entity>) {
        val payload = e.`object`
        if (payload is Player) {
            e.addCapability(
                ResourceLocation(ElytraBoosterMod.MODID, "input"),
                Provider(payload)
            )
        }
    }

    @SubscribeEvent
    fun onPlayerClone(e: PlayerEvent.Clone) {
        if (e.isWasDeath) return
        val old = e.original.getCapability(ElytraBoosterCapabilities.ELYTRA_INPUT)
        val new = e.player.getCapability(ElytraBoosterCapabilities.ELYTRA_INPUT)
        if (!(old.isPresent && new.isPresent)) return

        new.ifPresent { n ->
            old.ifPresent { o ->
                n.moveForward = o.moveForward
                n.moveStrafe = o.moveStrafe
            }
        }
    }
}
