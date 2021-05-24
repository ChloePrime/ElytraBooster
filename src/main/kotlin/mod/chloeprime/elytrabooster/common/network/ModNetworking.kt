package mod.chloeprime.elytrabooster.common.network

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import net.minecraft.util.ResourceLocation
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.concurrent.atomic.AtomicInteger

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModNetworking {
    lateinit var CHANNEL: SimpleChannel private set
    private const val VERSION = "1.0"
    private val idCounter = AtomicInteger(0)

    private val nextId get() = idCounter.getAndIncrement()

    private fun register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation(ElytraBoosterMod.MODID, "network"),
            { VERSION },
            { it == VERSION },
            { it == VERSION }
        )

        CHANNEL.messageBuilder(
            CElytraInputPacket::class.java,
            nextId,
            NetworkDirection.PLAY_TO_SERVER
        ).encoder(CElytraInputPacket::writeToBuffer)
            .decoder(::CElytraInputPacket)
            .consumer(CElytraInputPacket::handlePackage)
            .add()
    }

    @SubscribeEvent
    fun onFMLSetup(e: FMLCommonSetupEvent) {
        e.enqueueWork(::register)
    }
}
