package mod.chloeprime.elytrabooster.client.event

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import net.minecraft.client.player.LocalPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.Event

@OnlyIn(Dist.CLIENT)
class BoostedElytraInputEvent(
    val player: LocalPlayer,
    val input: IElytraInputCap,
    val strafeChanged: Boolean,
    val forwardChanged: Boolean
) : Event()