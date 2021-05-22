package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.text.IFormattableTextComponent

object TextFormats {
    /**
     * 求 [dividend] 占 [divisor] 的百分比，
     * 并保留两位小数，返回文本。
     */
    fun getPercentageText(dividend: Int, divisor: Int): String {
        return String.format("%.2f", dividend * 100F / divisor)
    }

    /**
     * 生存进度条（耐久条/储电量）的格式化文本
     *
     * @param current 当前值（当前剩余耐久/当前储电量等）
     * @param max 上限值（耐久上限/储电上限等）
     * @param unit 数值的物理单位
     */
    fun getProgressText(current: Int, max: Int, color: Int, unit: String = ""): IFormattableTextComponent {
        // 单位前空一格
        val unitText = if (unit.isEmpty()) {
            TEXT("")
        } else {
            TEXT(" ") + translated(unit)
        }

        return TEXT(current.toString()).applyStyle { setColor(color) } +
                unitText +
                TEXT(" / ").withColor(0xFFFFFF) +
                TEXT(max.toString()).withColor(color) +
                unitText +
                TEXT(" (").withColor(0xFFFFFF) +
                TEXT(getPercentageText(current, max)).withColor (color) +
                TEXT("%)").withColor(0xFFFFFF)
    }
}
