package mod.chloeprime.elytrabooster.common.caps

import net.minecraft.util.math.MathHelper
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage

interface ISettableEnergyStorage: IEnergyStorage{
    fun setEnergyStored(value: Int)
}

var IEnergyStorage.energy: Int
get() = this.energyStored
set(value) {
    if (this is ISettableEnergyStorage) {
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
