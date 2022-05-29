@file:OnlyIn(Dist.CLIENT)
package mod.chloeprime.elytrabooster.client.audio

import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.JET_ENGINE_START
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.JET_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.FUEL_ENGINE_START
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.FUEL_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.audio.ModSoundEvents.HALL_ENGINE_LOOP
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.client.audio.ISound
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.EquipmentSlotType.CHEST
import net.minecraft.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.util.IdentityHashMap

private val ItemToSound = IdentityHashMap<Item, (LivingEntity) -> ISound>(8)

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
object ElytraAudioRegistry {
    fun get(item: Item) = ItemToSound[item]
    fun new(entity: LivingEntity) = get(entity.getItemStackFromSlot(CHEST).item)?.invoke(entity)


    private val FUEL_START: (LivingEntity) -> ISound = {
        LoopedElytraSoundStart(it, FUEL_ENGINE_START.get(), FUEL_LOOP, 1f)
    }
    private val FUEL_LOOP: (LivingEntity) -> ISound = {
        LoopedElytraSound(it, FUEL_ENGINE_LOOP.get(), 1f)
    }
    private val JET_START_LESSER: (LivingEntity) -> ISound = {
        LoopedElytraSoundStart(it, JET_ENGINE_START.get(), JET_LOOP, 20f).apply {
            scaleVolume(0.75f)
        }
    }
    private val JET_START: (LivingEntity) -> ISound = {
        LoopedElytraSoundStart(it, JET_ENGINE_START.get(), JET_LOOP, 20f)
    }
    private val JET_LOOP: (LivingEntity) -> ISound = {
        LoopedElytraSound(it, JET_ENGINE_LOOP.get(), 0f)
    }
    private val HALL_LOOP: (LivingEntity) -> ISound = {
        LoopedElytraSound(it, HALL_ENGINE_LOOP.get(), 0f)
    }

    @SubscribeEvent
    fun init(e: FMLCommonSetupEvent) {
        ItemToSound[ModItems.BOOSTED_ELYTRA_FUEL_T1.get()] = JET_START
        ItemToSound[ModItems.BOOSTED_ELYTRA_FUEL_T2.get()] = FUEL_START
        ItemToSound[ModItems.BOOSTED_ELYTRA_FE_T1.get()] = JET_START_LESSER
        ItemToSound[ModItems.BOOSTED_ELYTRA_FE_T2.get()] = JET_START
    }
}