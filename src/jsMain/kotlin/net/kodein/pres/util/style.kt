package net.kodein.pres.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.document
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.StyleSheet
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.css.*


typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

interface TimingFunction: StylePropertyEnum {
    @Suppress("NOTHING_TO_INLINE")
    companion object {
        inline val Ease get() = TimingFunction("ease")
        inline val Linear get() = TimingFunction("linear")
        inline val EaseIn get() = TimingFunction("ease-in")
        inline val EaseOut get() = TimingFunction("ease-out")
        inline val EaseInOut get() = TimingFunction("ease-in-out")
        inline val StepStart get() = TimingFunction("step-start")
        inline val StepEnd get() = TimingFunction("step-end")
        inline fun Steps(count: Int, start: Boolean = false) = TimingFunction("steps($count, ${if (start) "start" else "end"})")
        inline fun CubicBezier(x1: Double, y1: Double, x2: Double, y2: Double) = TimingFunction("cubic-bezier($x1, $y1, $x2, $y2")
        inline val Initial get() = TimingFunction("initial")
        inline val Inherit get() = TimingFunction("inherit")
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun TimingFunction(value: String) = value.unsafeCast<TimingFunction>()

fun interface TransitionFunction {
    fun apply(): String
}

interface TransitionBuilder {
    operator fun String.invoke(
        duration: CSSTimeValue,
        timingFunction: TimingFunction = TimingFunction.Ease,
        delay: CSSTimeValue = 0.s
    )
}

private class TransitionBuilderImplementation : TransitionBuilder {
    private val transitions = mutableListOf<TransitionFunction>()

    override fun String.invoke(duration: CSSTimeValue, timingFunction: TimingFunction, delay: CSSTimeValue) {
        transitions.add { "$this $duration $timingFunction $delay" }
    }

    override fun toString(): String {
        return transitions.joinToString(", ") { it.apply() }
    }
}

@ExperimentalComposeWebApi
fun StyleBuilder.transition(transitionContext: TransitionBuilder.() -> Unit) {
    val transitionBuilder = TransitionBuilderImplementation()
    property("transition", transitionBuilder.apply(transitionContext).toString())
}

fun StyleBuilder.transformOrigin(value: String) {
    property("transform-origin", value)
}

fun StyleBuilder.zIndex(value: Int) {
    property("z-index", value)
}

fun StyleBuilder.visibility(value: String) {
    property("visibility", value)
}
