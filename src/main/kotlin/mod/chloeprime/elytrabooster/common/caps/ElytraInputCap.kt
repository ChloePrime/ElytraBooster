package mod.chloeprime.elytrabooster.common.caps

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import mod.chloeprime.elytrabooster.api.common.ElytraBoosterCapabilities
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

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
        return ElytraBoosterCapabilities.ELYTRA_INPUT!!.orEmpty(cap, LazyOptional.of { capInstance })
    }

    private val capInstance by lazy {
        ElytraInputCap(0F, 0F)
    }
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
internal object Registering {
    @SubscribeEvent
    fun onSetup(e: FMLCommonSetupEvent) {
        e.enqueueWork {
            CapabilityManager.INSTANCE.register(
                IElytraInputCap::class.java,
                object : Capability.IStorage<IElytraInputCap> {
                    override fun writeNBT(
                        capability: Capability<IElytraInputCap>,
                        instance: IElytraInputCap,
                        side: Direction
                    ): INBT {
                        val result = CompoundNBT()
                        result.putFloat("x", instance.moveStrafe)
                        result.putFloat("z", instance.moveForward)
                        return result
                    }

                    override fun readNBT(
                        capability: Capability<IElytraInputCap>,
                        instance: IElytraInputCap,
                        side: Direction,
                        nbt: INBT?
                    ) {
                        if (nbt is CompoundNBT) {
                            val x = nbt["x"]
                            val z = nbt["z"]
                            if (x is FloatNBT && z is FloatNBT) {
                                instance.moveStrafe = x.float
                                instance.moveForward = z.float
                            }
                        }
                    }
                }
            ) { ElytraInputCap(0F, 0F) }
        }
    }
}

@Mod.EventBusSubscriber
internal object Attaching {
    @SubscribeEvent
    fun onAttachCapability(e: AttachCapabilitiesEvent<Entity>) {
        val payload = e.`object`
        if (payload is PlayerEntity) {
            e.addCapability(
                ResourceLocation(ElytraBoosterMod.MODID, "input"),
                Provider(payload)
            )
        }
    }

    @SubscribeEvent
    fun onPlayerClone(e: PlayerEvent.Clone) {
        if (e.isWasDeath) return
        val old = e.original.getCapability(ElytraBoosterCapabilities.ELYTRA_INPUT!!)
        val new = e.player.getCapability(ElytraBoosterCapabilities.ELYTRA_INPUT!!)
        if (!(old.isPresent && new.isPresent)) return

        new.ifPresent { n ->
            old.ifPresent { o ->
                n.moveForward = o.moveForward
                n.moveStrafe = o.moveStrafe
            }
        }
    }
}
