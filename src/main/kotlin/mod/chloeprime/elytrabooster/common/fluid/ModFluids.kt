package mod.chloeprime.elytrabooster.common.fluid

import mod.chloeprime.elytrabooster.ElytraBoosterMod
import mod.chloeprime.elytrabooster.common.block.ModBlocks
import mod.chloeprime.elytrabooster.common.fluid.util.DeferredFluidRegister
import mod.chloeprime.elytrabooster.common.item.ModItemGroup
import mod.chloeprime.elytrabooster.common.item.ModItems
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material

object ModFluids {
    val REGISTRY = DeferredFluidRegister.create(
        ElytraBoosterMod.MODID, ModBlocks.REGISTRY, ModItems.REGISTRY
    )
    val JET_FUEL = REGISTRY.builder("jet_fuel")
        .itemGroup(ModItemGroup)
        .blockProperties(
            BlockBehaviour.Properties
                .of(Material.WATER)
                .strength(100F)
        )
        .disableBlockPlacement()
        .customTextureLocation("block/oil")
        .applyToFluidAttributes {
            color(0xC09EF94A.toInt())
            density(4000)
            viscosity(4000)
        }
        .applyToFluidProperties {
            explosionResistance(100F)
        }
        .register()
    val ROCKET_FUEL = REGISTRY.builder("rocket_fuel")
        .itemGroup(ModItemGroup)
        .blockProperties(
            BlockBehaviour.Properties
                .of(Material.WATER)
                .strength(100F)
        )
        .disableBlockPlacement()
        .customTextureLocation("block/oil")
        .applyToFluidAttributes {
            color(0xE0CC9F13.toInt())
            density(4000)
            viscosity(4000)
        }
        .applyToFluidProperties {
            explosionResistance(100F)
        }
        .register()
}
