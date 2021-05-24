package mod.chloeprime.elytrabooster.api.common

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object ModCaps {
    @JvmField
    @CapabilityInject(IElytraInputCap::class)
    var ELYTRA_INPUT: Capability<IElytraInputCap>? = null
}
