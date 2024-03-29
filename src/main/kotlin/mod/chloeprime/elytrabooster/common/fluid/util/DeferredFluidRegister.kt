package mod.chloeprime.elytrabooster.common.fluid.util

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.ForgeFlowingFluid
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.commons.lang3.mutable.MutableObject
import java.util.*

class DeferredFluidRegister private constructor(
    val modid: String,
    private val blocks: DeferredRegister<Block>,
    private val items: DeferredRegister<Item>
) {
    private val fluids = DeferredRegister.create(ForgeRegistries.FLUIDS, modid)!!
    private val _entries = LinkedList<FluidRegistryEntry>()
    val entries: Collection<FluidRegistryEntry> = _entries

    companion object {
        @JvmStatic
        fun create(
            modid: String,
            blocks: DeferredRegister<Block>,
            items: DeferredRegister<Item>
        ) = DeferredFluidRegister(modid, blocks, items)

        private val VANILLA_WATER_TEXTURE = ResourceLocation("block/water")
        private const val DISABLE_BLOCK_PLACEMENT = 1
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
         * 流体所属桶的 [CreativeModeTab]
         */
        private lateinit var itemGroup: CreativeModeTab
        fun itemGroup(value: CreativeModeTab): Builder {
            itemGroup = value
            return this
        }

        /**
         * 流体方块的属性
         */
        private lateinit var blockProperties: BlockBehaviour.Properties
        fun blockProperties(value: BlockBehaviour.Properties): Builder {
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

        private var customTexture: ResourceLocation? = null
        fun useVanillaWaterTexture(): Builder {
            customTexture = VANILLA_WATER_TEXTURE
            return this
        }

        fun customTextureLocation(texPath: String): Builder {
            customTexture = ResourceLocation(modid, texPath)
            return this
        }

        fun customTextureLocation(texPath: ResourceLocation): Builder {
            customTexture = texPath
            return this
        }

        private var disabledFlags = 0
        fun disableBlockPlacement(): Builder {
            disabledFlags = disabledFlags or DISABLE_BLOCK_PLACEMENT
            return this
        }

        /**
         * 执行自动化流体注册。
         * 一次性注册流体源+流动流体+流体方块+桶
         */
        fun register(): FluidRegistryEntry {
            val propRef = MutableObject<ForgeFlowingFluid.Properties?>(null)
            // 注册流体

            /**
             * 水源流体
             */
            val source = fluids.register(name) {
                ForgeFlowingFluid.Source(propRef.value!!)
            }!!

            /**
             * 流动的流体
             */
            val flowing = fluids.register("${name}_flowing") {
                ForgeFlowingFluid.Flowing(propRef.value!!)
            }!!

            // 注册方块
            val adjustedBlockProps = blockProperties.noCollission().noDrops()
            val enableBlock = disabledFlags and DISABLE_BLOCK_PLACEMENT == 0
            val block = if (enableBlock) {
                blocks.register(name) {
                    LiquidBlock(source, adjustedBlockProps)
                }
            } else null
            // 注册桶
            val bucket = items.register("${name}_bucket") {
                BucketItem(source, Item.Properties().tab(itemGroup).craftRemainder(Items.BUCKET))
            }!!
            // 设置流体的各类属性
            val texPathBase = (customTexture ?: ResourceLocation(modid, "blocks/${name}"))
            val stillTexture =
                ResourceLocation(texPathBase.namespace, "${texPathBase.path}_still")
            val flowingTexture =
                ResourceLocation(texPathBase.namespace, "${texPathBase.path}_flow")
            val overlay =
                ResourceLocation(texPathBase.namespace, "${texPathBase.path}_overlay")
            // Attributes
            val attributes = FluidAttributes
                .builder(stillTexture, flowingTexture)
                .overlay(overlay)
                .apply { attributeOperator() }
            // Properties
            var prop = ForgeFlowingFluid.Properties(
                source, flowing, attributes
            ).bucket(bucket).apply { propertiesOperator() }
            if (enableBlock) {
                prop = prop.block(block!!)
            }
            propRef.value = prop
            // 生成结果
            return FluidRegistryEntry(name, source, flowing, block, bucket).also {
                _entries.add(it)
            }
        }
    }
}