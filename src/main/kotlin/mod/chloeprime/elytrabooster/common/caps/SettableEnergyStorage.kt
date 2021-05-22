package mod.chloeprime.elytrabooster.common.caps

import net.minecraft.util.math.MathHelper
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage

class SettableEnergyStorage(
    capacity: Int,
    maxReceive: Int,
    maxExtract: Int,
    energy: Int
): EnergyStorage(capacity, maxReceive, maxExtract, energy) {
    constructor(capacity: Int) : this(capacity, capacity, capacity, 0)

    fun setEnergyStored(energyIn: Int) {
        energy = energyIn
    }
}

var IEnergyStorage.energy: Int
get() = this.energyStored
set(value) {
    if (this is SettableEnergyStorage) {
        this.energyStored = MathHelper.clamp(value, 0, this.maxEnergyStored)
        return
    }
    val delta = value - this.energyStored
    if (delta > 0) {
        this.receiveEnergy(delta, false)
    } else {
        this.extractEnergy(-delta, false)
    }
}
