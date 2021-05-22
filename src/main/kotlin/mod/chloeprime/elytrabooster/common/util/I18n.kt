package mod.chloeprime.elytrabooster.common.util

import com.google.common.cache.CacheBuilder
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import java.util.concurrent.TimeUnit

object I18n {
    private val cache =CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.SECONDS)
        .build<String, ITextComponent>()

    fun format(key: String, vararg params: Any) : ITextComponent {
        if (params.isEmpty()) {
            return cache.get(key) {
                generate(key, params)
            }
        }
        return generate(key, params)
    }

    private fun generate(key: String, vararg params: Any): ITextComponent {
        val rawJson = I18n.format(key, *params)
        return ITextComponent.Serializer.getComponentFromJson(
            rawJson
        ) ?: StringTextComponent(rawJson)
    }
}
