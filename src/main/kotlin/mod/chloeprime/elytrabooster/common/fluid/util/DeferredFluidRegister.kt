package mod.chloeprime.elytrabooster.common.fluid.util

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.FlowingFluidBlock
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.ForgeFlowingFluid
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.commons.lang3.mutable.MutableObject

class DeferredFluidRegister private constructor(
    val modid: String,
    private val blocks: DeferredRegister<Block>,
    private val items: DeferredRegister<Item>
) {
    private val fluids = DeferredRegister.create(ForgeRegistries.FLUIDS, modid)!!

    companion object {
        @JvmStatic
        fun create(
            modid: String,
            blocks: DeferredRegister<Block>,
            items: DeferredRegister<Item>
        ) = DeferredFluidRegister(modid, blocks, items)
    }

    fun enqueueToBus(bus: IEventBus) {
        fluids.register(bus)
    }

    fun builder(name: String) = Builder(name)

    /**
     * @param name 流体名称
     */
    inner class Builder internal constructor(
        private val name: String,
    ) {
        /**
         * 流体所属桶的 [ItemGroup]
         */
        private lateinit var itemGroup: ItemGroup
        fun itemGroup(value: ItemGroup): Builder {
            itemGroup = value
            return this
        }

        /**
         * 流体方块的属性
         */
        private lateinit var blockProperties: AbstractBlock.Properties
        fun blockProperties(value: AbstractBlock.Properties): Builder {
            blockProperties = value
            return this
        }

        /**
         * 对流体属性（[FluidAttributes.Builder]]）的追加操作
         */
        private var attributeOperator: FluidAttributes.Builder.() -> Unit = {}
        fun applyToFluidAttributes(value: FluidAttributes.Builder.() -> Unit): Builder {
            attributeOperator = value
            return this
        }

        /**
         * 对流体属性（[ForgeFlowingFluid.Properties]]）的追加操作
         */
        private var propertiesOperator: ForgeFlowingFluid.Properties.() -> Unit = {}
        fun applyToFluidProperties(value: ForgeFlowingFluid.Properties.() -> Unit): Builder {
            propertiesOperator = value
            return this
        }

        /**
         * 执行自动化流体注册。
         * 一次性注册流体源+流动流体+流体方块+桶
         */
        fun register(): FluidRegistryEntry {
            val props = MutableObject<ForgeFlowingFluid.Properties?>(null)
            // 注册流体

            /**
             * 水源流体
             */
            val source = fluids.register(name) {
                ForgeFlowingFluid.Source(props.value!!)
            }!!

            /**
             * 流动的流体
             */
            val flowing = fluids.register("${name}_flowing") {
                ForgeFlowingFluid.Flowing(props.value!!)
            }!!

            // 注册方块
            val adjustedBlockProps = blockProperties.doesNotBlockMovement().noDrops()
            val block = blocks.register(name) {
                FlowingFluidBlock(source, adjustedBlockProps)
            }
            // 注册桶
            val bucket = items.register("${name}_bucket") {
                BucketItem(source, Item.Properties().group(itemGroup).containerItem(Items.BUCKET))
            }!!
            // 设置流体的各类属性
            val stillTexture = ResourceLocation(modid, "blocks/${name}_still")
            val flowingTexture = ResourceLocation(modid, "blocks/${name}_flow")
            // Attributes
            val attributes = FluidAttributes
                .builder(stillTexture, flowingTexture)
                .apply { attributeOperator() }
            // Properties
            props.value = ForgeFlowingFluid.Properties(
                source, flowing, attributes
            ).bucket(bucket).block(block).apply { propertiesOperator() }
            // 生成结果
            return FluidRegistryEntry(name, source, flowing, block, bucket)
        }
    }
}