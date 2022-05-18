package net.kodein.pres.util

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*


public typealias Style = StyleScope.() -> Unit

public typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

internal fun interface TransitionFunction {
    fun apply(): String
}

public interface TransitionBuilder {
    public operator fun String.invoke(
        duration: CSSTimeValue,
        timingFunction: AnimationTimingFunction = AnimationTimingFunction.Ease,
        delay: CSSTimeValue = 0.s
    )
}

private class TransitionBuilderImplementation : TransitionBuilder {
    private val transitions = mutableListOf<TransitionFunction>()

    override fun String.invoke(duration: CSSTimeValue, timingFunction: AnimationTimingFunction, delay: CSSTimeValue) {
        transitions.add { "$this $duration $timingFunction $delay" }
    }

    override fun toString(): String {
        return transitions.joinToString(", ") { it.apply() }
    }
}

@ExperimentalComposeWebApi
public fun StyleScope.transition(transitionContext: TransitionBuilder.() -> Unit) {
    val transitionBuilder = TransitionBuilderImplementation()
    property("transition", transitionBuilder.apply(transitionContext).toString())
}

public fun StyleScope.transformOrigin(value: String) {
    property("transform-origin", value)
}

public fun StyleScope.zIndex(value: Int) {
    property("z-index", value)
}

public interface Visibility : StylePropertyEnum {
    public companion object {
        public inline val Visible: Visibility get() = Visibility("ease")
        public inline val Hidden: Visibility get() = Visibility("hidden")
        public inline val Collapse: Visibility get() = Visibility("collapse")

        public inline val Inherit: Visibility get() = Visibility("inherit")
        public inline val Initial: Visibility get() = Visibility("initial")
        public inline val Unset: Visibility get() = Visibility("unset")
    }
}

@Suppress("NOTHING_TO_INLINE")
public inline fun Visibility(value: String): Visibility = value.unsafeCast<Visibility>()

public fun StyleScope.visibility(value: Visibility) {
    property("visibility", value)
}
