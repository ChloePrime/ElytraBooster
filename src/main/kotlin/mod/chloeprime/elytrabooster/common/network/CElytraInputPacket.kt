package mod.chloeprime.elytrabooster.common.network

import mod.chloeprime.elytrabooster.api.common.ElytraBoosterApi
import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * 将客户端的鞘翅输入发往服务端
 */
class CElytraInputPacket(
    private val strafe: Float,
    private val forward: Float
) {
    constructor(packet: FriendlyByteBuf) : this(
        // 防止外挂端输入-1到1之外的值
        Mth.clamp(packet.readFloat(), -1F, 1F),
        Mth.clamp(packet.readFloat(), -1F, 1F)
    )
    constructor(input: IElytraInputCap) : this(input.moveStrafe, input.moveForward)

    fun writeToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeFloat(strafe)
        buffer.writeFloat(forward)
    }

    /**
     * 在服务端执行
     */
    fun handlePackage(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender ?: return@enqueueWork
            ElytraBoosterApi.getElytraInputOrNull(sender)?.let {
                it.moveStrafe = this.strafe
                it.moveForward = this.forward
            }
        }
        ctx.get().packetHandled = true
    }
}
