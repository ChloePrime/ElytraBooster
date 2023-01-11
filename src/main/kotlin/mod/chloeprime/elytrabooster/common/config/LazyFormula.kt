package mod.chloeprime.elytrabooster.common.config

import mod.chloeprime.elytrabooster.api.common.IElytraInputCap
import org.apache.logging.log4j.LogManager
import org.openjdk.nashorn.api.scripting.ClassFilter
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import java.util.function.Supplier
import java.util.function.ToIntFunction
import javax.script.Compilable
import javax.script.ScriptContext
import javax.script.ScriptException
import kotlin.math.abs

/**
 * 数学公式。
 * 自变量为 x, y，返回一个数。
 * 公式需要符合JavaScript语法，且输入的JavaScript必须是纯函数，
 * 即输入同样的x，y，输出的结果必须相同。
 */
class LazyFormula(
    private val javaScript: Supplier<String>
): (Float, Float) -> Int {
    companion object {
        private val LOGGER = LogManager.getLogger()
        private val scriptFactory = NashornScriptEngineFactory()

        @Suppress("RedundantSamConstructor")
        private fun createEngine() = scriptFactory.getScriptEngine(ClassFilter { false })

        /*
        /**
         * 如果公式中包含这些单词，
         * 则将这个公式视作病毒脚本。
         */
        private val NG_WORD_LIST = arrayOf(
            "var", "\"", "'", "\n", "{", "[", "if", "while", "for", "eval", "let", "const"
        )
        */
    }

    /*
    init {
        if (NG_WORD_LIST.any {
                it in javaScript
            }) {
            throw SecurityException("Malicious Formula Script")
        }
    }
    */

    private val scriptEngine = createEngine()
    private val compiled by lazy {
        wrapScriptError {
            (scriptEngine as? Compilable)?.compile(javaScript.get())
        }
    }
    private var lastX = -Float.MAX_VALUE
    private var lastY = -Float.MAX_VALUE
    private var lastResult = -1

    override fun invoke(x: Float, y: Float): Int {
        if (x == lastX && y == lastY) {
            return lastResult
        }
        lastX = x
        lastY = y
        val bindings = scriptEngine.createBindings().apply {
            put("x", x)
            put("y", y)
        }
        scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)
        lastResult = wrapScriptError {
            val evalResult = (compiled?.eval() ?: scriptEngine.eval(javaScript.get()))
            (evalResult as Number).toInt()
        } ?: -1
        return lastResult
    }

    private inline fun <T> wrapScriptError(block: () -> T): T? {
        return try {
            block()
        } catch (e: ScriptException) {
            LOGGER.error("Illegal formula: $javaScript, formula must be valid JavaScript", e)
            null
        }
    }
}

fun LazyFormula.wrap() = ToIntFunction<IElytraInputCap> { cap ->
    this(abs(cap.moveStrafe), abs(cap.moveForward))
}
