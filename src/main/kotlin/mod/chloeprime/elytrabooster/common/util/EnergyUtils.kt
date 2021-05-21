package mod.chloeprime.elytrabooster.common.util

import net.minecraftforge.energy.IEnergyStorage

fun IEnergyStorage.setEnergy(energy: Int) {
    val delta = energy - this.energyStored
    if (delta > 0) {
        this.receiveEnergy(delta, false)
    } else {
        this.extractEnergy(-delta, false)
    }
}
