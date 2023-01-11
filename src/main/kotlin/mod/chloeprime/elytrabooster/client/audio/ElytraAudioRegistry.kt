@file:OnlyIn(Dist.CLIENT)
package mod.chloeprime.elytrabooster.client.audio

import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.FUEL_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.FUEL_ENGINE_START
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.HALL_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.JET_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.JET_ENGINE_START
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.util.*

private val itemToSound = IdentityHashMap<Item, (LivingEntity) -> SoundInstance>(8)

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
object ElytraAudioRegistry {
    fun get(item: Item) = itemToSound[item]
    fun new(entity: LivingEntity) = get(entity.getItemBySlot(EquipmentSlot.CHEST).item)?.invoke(entity)


    private val FUEL_START: (LivingEntity) -> SoundInstance = {
        LoopedElytraSoundStart(it, FUEL_ENGINE_START.get(), FUEL_LOOP, 1f)
    }
    private val FUEL_LOOP: (LivingEntity) -> SoundInstance = {
        LoopedElytraSound(it, FUEL_ENGINE_LOOP.get(), 1f)
    }
    private val JET_START_LESSER: (LivingEntity) -> SoundInstance = {
        LoopedElytraSoundStart(it, JET_ENGINE_START.get(), JET_LOOP, 20f).apply {
            scaleVolume(0.75f)
        }
    }
    private val JET_START: (LivingEntity) -> SoundInstance = {
        LoopedElytraSoundStart(it, JET_ENGINE_START.get(), JET_LOOP, 20f)
    }
    private val JET_LOOP: (LivingEntity) -> SoundInstance = {
        LoopedElytraSound(it, JET_ENGINE_LOOP.get(), 0f)
    }
    private val HALL_LOOP: (LivingEntity) -> SoundInstance = {
        LoopedElytraSound(it, HALL_ENGINE_LOOP.get(), 0f)
    }

    @SubscribeEvent
    fun init(e: FMLCommonSetupEvent) {
        itemToSound[ModItems.BOOSTED_ELYTRA_FUEL_T1.get()] = JET_START
        itemToSound[ModItems.BOOSTED_ELYTRA_FUEL_T2.get()] = FUEL_START
        itemToSound[ModItems.BOOSTED_ELYTRA_FE_T1.get()] = JET_START_LESSER
        itemToSound[ModItems.BOOSTED_ELYTRA_FE_T2.get()] = JET_START
    }
}