package net.kodein.pres

import net.kodein.pres.util.TransitionBuilder
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


public interface Transition {
    public val cssTransition: TransitionBuilder.() -> Unit
    public val hiddenStyle: StyleBuilder.() -> Unit
}

public fun <TElement : HTMLElement> AttrsBuilder<TElement>.hiddenIf(condition: Boolean, transition: Transition) {
    classes(PresStyle.css {
        transition {
            transition.cssTransition.invoke(this)
        }
    })
    if (condition) {
        style {
            transition.hiddenStyle.invoke(this)
        }
    }
}

public fun <TElement : HTMLElement> AttrsBuilder<TElement>.shownIf(condition: Boolean, transition: Transition): Unit =
    hiddenIf(!condition, transition)

public object Transitions {
    public class Fade(private val duration: Duration) : Transition {
        override val cssTransition: TransitionBuilder.() -> Unit = {
            "opacity"(duration.inWholeMilliseconds.ms)
        }

        override val hiddenStyle: StyleBuilder.() -> Unit = {
            opacity(0)
        }
    }
    public val fade: Transition = Fade(milliseconds(300))

    public class Grow(private val duration: Duration) : Transition {
        override val cssTransition: TransitionBuilder.() -> Unit = {
            "opacity"(duration.inWholeMilliseconds.ms)
            "transform"(duration.inWholeMilliseconds.ms, AnimationTimingFunction.cubicBezier(.15, .65, .45, 2.2))
        }

        override val hiddenStyle: StyleBuilder.() -> Unit = {
            opacity(0)
            transform { scale(0.25) }
        }
    }
    public val grow: Transition = Grow(milliseconds(500))

    public class FontGrow(private val duration: Duration): Transition {
        override val cssTransition: TransitionBuilder.() -> Unit = {
            "font-size"(duration.inWholeMilliseconds.ms)
            "line-height"(duration.inWholeMilliseconds.ms)
        }
        override val hiddenStyle: StyleBuilder.() -> Unit = {
            fontSize(0.em)
            lineHeight(0.em)
        }
    }
    public val fontGrow: Transition = FontGrow(milliseconds(300))

    public class Stamp(private val duration: Duration) : Transition {
        override val cssTransition: TransitionBuilder.() -> Unit = {
            "opacity"(duration.inWholeMilliseconds.ms)
            "transform"(duration.inWholeMilliseconds.ms, AnimationTimingFunction.EaseIn)
        }
        override val hiddenStyle: StyleBuilder.() -> Unit = {
            opacity(0)
            transform { scale(1.8) }
        }
    }
    public val stamp: Transition = Stamp(Duration.milliseconds(400))

}
