package mod.chloeprime.elytrabooster.common.fluid

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.block.ModBlocks
import mod.chloeprime.elytrabooster.common.fluid.util.DeferredFluidRegister
import mod.chloeprime.elytrabooster.common.item.ModItemGroup
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.block.AbstractBlock
import net.minecraft.block.material.Material

object ModFluids {
    val REGISTRY = DeferredFluidRegister.create(
        ElytraBoosterMod.MODID, ModBlocks.REGISTRY, ModItems.REGISTRY
    )
    val JET_FUEL = REGISTRY.builder("jet_fuel")
        .itemGroup(ModItemGroup)
        .blockProperties(
            AbstractBlock.Properties
                .create(Material.WATER)
                .hardnessAndResistance(100F)
        )
        .disableBlockPlacement()
        .customTextureLocation("block/oil")
        .applyToFluidAttributes {
            color(0xE09EF94A.toInt())
            density(4000)
            viscosity(4000)
        }
        .applyToFluidProperties {
            explosionResistance(100F)
        }
        .register()
}