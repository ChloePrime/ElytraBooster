package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.text.TranslationTextComponent

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
     * @param langKey 本地化key
     * @param current 当前值（当前剩余耐久/当前储电量等）
     * @param max 上限值（耐久上限/储电上限等）
     */
    fun getTranslatedProgressText(langKey: String, current: Int, max: Int)
    = TranslationTextComponent(langKey, current, max, getPercentageText(current, max))
}