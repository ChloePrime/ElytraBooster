package mod.chloeprime.elytrabooster.api.common

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import java.lang.IllegalStateException

object ModCaps {
    @JvmField
    @CapabilityInject(IElytraInputCap::class)
    var ELYTRA_INPUT: Capability<IElytraInputCap>? = null
}
