package mod.chloeprime.elytrabooster.common.item

import net.minecraft.item.Item

open class ColoredItem(
    properties: Properties,
    override val color: Int
): Item(properties), IColoredItem