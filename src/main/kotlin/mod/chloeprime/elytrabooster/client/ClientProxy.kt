package mod.chloeprime.elytrabooster.client

import mod.chloeprime.elytrabooster.client.item.ElytraBaseRenderProperties
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.IItemRenderProperties

/**
 * Rp = Render Properties
 */
internal object ClientProxy {
    fun localPlayer(): Player? = Minecraft.getInstance().player
    fun elytraBaseRp(): IItemRenderProperties = ElytraBaseRenderProperties
}