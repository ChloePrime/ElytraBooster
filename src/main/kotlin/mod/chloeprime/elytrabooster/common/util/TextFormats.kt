package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.text.IFormattableTextComponent
import net.minecraft.util.text.ITextComponent

/**
 * 本Mod中部分文本的统一化生成方法
 */
object TextFormats {
    private val units = arrayOf("K", "M", "G")

    /**
     * 更好地表示大数。
     * 缩减后整数部分数字 <10 时，保留两位小数，
     * 否则保留一位小数
     */
    fun formatBigNumber(num: Int): String {
        var i = num
        var decimal = 0F
        var divideCount = 0
        while ((i <= -1000 || i >= 1000) && divideCount < units.size) {
            ++divideCount
            decimal = i / 1000F
            i /= 1000
        }
        if (divideCount > 0) {
            return String.format(
                if (i < 10) "%.2f" else "%.1f",
                decimal
            ) + units[divideCount - 1]
        }
        return i.toString()
    }

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
    fun getProgressText(current: Int, max: Int, color: Int, unit: String = ""): ITextComponent {
        // 单位前空一格
        val unitText = if (unit.isEmpty()) {
            TEXT("")
        } else {
            translated(unit)
        }

        return TEXT(formatBigNumber(current)).applyStyle { setColor(color) } +
                unitText +
                TEXT(" / ").withColor(0xFFFFFF) +
                TEXT(formatBigNumber(max)).withColor(color) +
                unitText +
                TEXT(" (").withColor(0xFFFFFF) +
                TEXT(getPercentageText(current, max)).withColor (color) +
                TEXT("%)").withColor(0xFFFFFF)
    }
}
