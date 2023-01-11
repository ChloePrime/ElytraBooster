package mod.chloeprime.elytrabooster.api.common

import mod.chloeprime.elytrabooster.common.util.findCapabilityKey
import net.minecraftforge.common.capabilities.Capability

object ElytraBoosterCapabilities {
    val ELYTRA_INPUT: Capability<IElytraInputCap> = findCapabilityKey()
}