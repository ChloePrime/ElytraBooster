package mod.chloeprime.elytrabooster.client.event

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.Event

@OnlyIn(Dist.CLIENT)
class BoostedElytraInputEvent(
    val player: ClientPlayerEntity,
    val input: IElytraInputCap,
    val strafeChanged: Boolean,
    val forwardChanged: Boolean
) : Event()