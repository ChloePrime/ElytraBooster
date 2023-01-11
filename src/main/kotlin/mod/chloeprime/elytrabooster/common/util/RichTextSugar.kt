package mod.chloeprime.elytrabooster.common.util

import net.minecraft.network.chat.*
import java.util.*
import java.util.function.UnaryOperator

/**
 * @UE4
 */
@Suppress("FunctionName")
fun TEXT(string: String): MutableComponent =
    if(string.isEmpty()) EMPTY_TEXT else TextComponent(string)

/**
 * 空字符串对应的TextComponent
 */
private val EMPTY_TEXT = TextComponent("")

/**
 * 更简洁地构建翻译字符串
 */
fun translated(langKey: String): MutableComponent =
    if (langKey.isEmpty()) EMPTY_TEXT else TranslatableComponent(langKey)

fun translated(langKey: String, vararg args: Any) =
    TranslatableComponent(langKey, *args)

/**
 * 针对MC富文本的字符串加法
 */
operator fun Component.plus(other: Component): Component =
    this.cast().append(other)

/**
 * 针对MC富文本的字符串加法
 * 忽视空字符串
 */
operator fun Component.plus(other: String): Component {
    if (other.isEmpty()) return this.cast()
    return this.cast().append(other)
}

fun Component.withColor(c: Int): Component =
    this.cast().withStyle { it.withColor(TextColor.fromRgb(c)) }

fun Component.applyStyle(action: StylePipeline.() -> Unit): Component {
    val pipeline = StylePipeline()
    pipeline.action()
    return this.cast().withStyle(pipeline)
}

class StylePipeline internal constructor(): UnaryOperator<Style> {

    fun setColor(c: Int) = pipeline.add {
        it.withColor(TextColor.fromRgb(c))
    }

    fun setBold(b: Boolean?) = pipeline.add {
        it.withBold(b)
    }

    fun setItalic(b: Boolean?) = pipeline.add {
        it.withItalic(b)
    }

    fun setUnderlined(b: Boolean?) = pipeline.add {
        it.withUnderlined(b)
    }

    fun setStrikethrough(b: Boolean?) = pipeline.add {
        it.withStrikethrough(b)
    }

    fun setObfuscated(b: Boolean?) = pipeline.add {
        it.withObfuscated(b)
    }

    private val pipeline = LinkedList<(Style) -> Style>()
    override fun apply(p1: Style): Style {
        var result = p1
        pipeline.forEach {
            result = it(result)
        }
        return result
    }
}

private fun Component.cast(): MutableComponent =
    if (this is MutableComponent) this
    else TextComponent("").append(this)
