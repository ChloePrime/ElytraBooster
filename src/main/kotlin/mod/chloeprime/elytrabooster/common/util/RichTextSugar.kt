package mod.chloeprime.elytrabooster.common.util

import net.minecraft.util.text.*
import java.util.*

/**
 * @UE4
 */
@Suppress("FunctionName")
fun TEXT(string: String): IFormattableTextComponent =
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
operator fun ITextComponent.plus(other: ITextComponent): ITextComponent =
    this.cast().appendSibling(other)

/**
 * 针对MC富文本的字符串加法
 * 忽视空字符串
 */
operator fun ITextComponent.plus(other: String): ITextComponent {
    if (other.isEmpty()) return this.cast()
    return this.cast().appendString(other)
}

fun ITextComponent.withColor(c: Int): ITextComponent =
    this.cast().modifyStyle { it.setColor(Color.fromInt(c)) }

fun ITextComponent.applyStyle(action: StylePipeline.() -> Unit): ITextComponent {
    val pipeline = StylePipeline()
    pipeline.action()
    return this.cast().modifyStyle(pipeline)
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

private fun ITextComponent.cast(): IFormattableTextComponent =
    if (this is IFormattableTextComponent) this
    else StringTextComponent("").appendSibling(this)
