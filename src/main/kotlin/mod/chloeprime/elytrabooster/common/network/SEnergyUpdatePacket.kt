package mod.chloeprime.elytrabooster.common.network

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.common.util.setEnergy
import net.minecraft.client.Minecraft
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.network.PacketBuffer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier
import javax.management.openmbean.InvalidOpenTypeException

/**
 * 将服务端装备栏的FE变化发往客户端
 */
class SEnergyUpdatePacket(
    // 以 byte 存储
    val slot: EquipmentSlotType,
    // 以 var int 存储
    val energy: Int
) {
    /**
     * Encoder
     */
    fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte(slot.ordinal)
        buffer.writeVarInt(energy)
    }

    /**
     * Decoder
     */
    constructor(packet: PacketBuffer): this(
        try {
            EquipmentSlotType.values()[packet.readByte().toInt()]
        } catch (e: IndexOutOfBoundsException) {
            throw InvalidOpenTypeException("Invalid package with wrong slot index")
        },
        packet.readVarInt()
    )

    companion object {
        @CapabilityInject(IEnergyStorage::class)
        lateinit var ENERGY_CAP: Capability<IEnergyStorage>
    }

    /**
     * Consumer
     */
    fun handlePackage(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val player = Minecraft.getInstance().player ?: return@enqueueWork
            player.getItemStackFromSlot(slot).getCapability(ENERGY_CAP).ifPresent {
                it.setEnergy(this.energy)
            }
        }
        ctx.get().packetHandled = true
    }
}
