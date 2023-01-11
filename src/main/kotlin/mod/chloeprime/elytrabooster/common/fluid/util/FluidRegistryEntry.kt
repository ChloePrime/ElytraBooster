package mod.chloeprime.elytrabooster.common.fluid.util

import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.FlowingFluid
import net.minecraftforge.registries.RegistryObject

class FluidRegistryEntry(
    val name: String,
    /**
     * 水源流体，
     * 注册名 [name]，
     * 材质位置 blocks/[name]_still
     */
    val source: RegistryObject<out FlowingFluid>,
    /**
     * 流动的水流体，
     * 注册名 [name]_flowing，
     * 材质位置 blocks/[name]_flow
     */
    val flowing: RegistryObject<out FlowingFluid>,
    /**
     * 流体方块，如果流体注册时选择了无法被放置，那么方块将为 null，
     * 注册名 [name]
     */
    val block: RegistryObject<out LiquidBlock>?,
    /**
     * 流体桶，
     * 注册名 [name]_bucket
     */
    val bucket: RegistryObject<out BucketItem>
)
