package mod.chloeprime.elytrabooster.common.caps

import net.minecraft.util.Mth
import net.minecraftforge.energy.IEnergyStorage

interface ISettableEnergyStorage: IEnergyStorage{
    fun setEnergyStored(value: Int)
}

var IEnergyStorage.energy: Int
get() = this.energyStored
set(value) {
    if (this is ISettableEnergyStorage) {
        this.energyStored = Mth.clamp(value, 0, this.maxEnergyStored)
        return
    }
    val delta = value - this.energyStored
    if (delta > 0) {
        this.receiveEnergy(delta, false)
    } else {
        this.extractEnergy(-delta, false)
    }
}
