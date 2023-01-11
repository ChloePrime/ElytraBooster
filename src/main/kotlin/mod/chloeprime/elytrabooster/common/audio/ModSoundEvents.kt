package mod.chloeprime.elytrabooster.common.audio

import mod.chloeprime.elytrabooster.ElytraBoosterMod.MODID
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModSoundEvents {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID)!!
    val FUEL_ENGINE_START = REGISTRY.register("fuel_engine_start") {
        SoundEvent(ResourceLocation(MODID, "fuel_engine_start"))
    }!!
    val FUEL_ENGINE_LOOP = REGISTRY.register("fuel_engine_loop") {
        SoundEvent(ResourceLocation(MODID, "fuel_engine_loop"))
    }!!
    val JET_ENGINE_START = REGISTRY.register("jet_engine_start") {
        SoundEvent(ResourceLocation(MODID, "jet_engine_start"))
    }!!
    val JET_ENGINE_LOOP = REGISTRY.register("jet_engine_loop") {
        SoundEvent(ResourceLocation(MODID, "jet_engine_loop"))
    }!!
    val HALL_ENGINE_LOOP = REGISTRY.register("hall_engine_loop") {
        SoundEvent(ResourceLocation(MODID, "hall_engine_loop"))
    }!!
}