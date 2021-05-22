package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.text.*
import java.util.*

/**
 * @UE4
 */
@Suppress("FunctionName")
fun TEXT(string: String) =
    if(string.isEmpty()) EMPTY_TEXT else StringTextComponent(string)

/**
 * 空字符串对应的TextComponent
 */
private val EMPTY_TEXT =
    StringTextComponent("")

/**
 * 更简洁地构建翻译字符串
 */
fun translated(langKey: String): IFormattableTextComponent =
    if (langKey.isEmpty()) EMPTY_TEXT else TranslationTextComponent(langKey)

fun translated(langKey: String, vararg args: Any) =
    TranslationTextComponent(langKey, *args)

/**
 * 针对MC富文本的字符串加法
 */
operator fun IFormattableTextComponent.plus(other: IFormattableTextComponent): IFormattableTextComponent =
    this.appendSibling(other)

/**
 * 针对MC富文本的字符串加法
 * 忽视空字符串
 */
operator fun IFormattableTextComponent.plus(other: String): IFormattableTextComponent {
    if (other.isEmpty()) return this
    return this.appendString(other)
}

fun IFormattableTextComponent.withColor(c: Int): IFormattableTextComponent =
    this.modifyStyle { it.setColor(Color.fromInt(c)) }

fun IFormattableTextComponent.applyStyle(action: StylePipeline.() -> Unit): IFormattableTextComponent {
    val pipeline = StylePipeline()
    pipeline.action()
    return this.modifyStyle(pipeline)
}

class StylePipeline internal constructor(): (Style) -> Style {

    fun setColor(c: Int) = pipeline.add {
        it.setColor(Color.fromInt(c))
    }

    fun setBold(b: Boolean?) = pipeline.add {
        it.setBold(b)
    }

    fun setItalic(b: Boolean?) = pipeline.add {
        it.setItalic(b)
    }

    fun setUnderlined(b: Boolean?) = pipeline.add {
        it.setUnderlined(b)
    }

    fun setStrikethrough(b: Boolean?) = pipeline.add {
        it.setStrikethrough(b)
    }

    fun setObfuscated(b: Boolean?) = pipeline.add {
        it.setObfuscated(b)
    }

    private val pipeline = LinkedList<(Style) -> Style>()
    override fun invoke(p1: Style): Style {
        var result = p1
        pipeline.forEach {
            result = it(result)
        }
        return result
    }
}
