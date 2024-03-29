package org.kodein.compose.html.pres

import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.kodein.compose.html.css.css
import org.w3c.dom.HTMLElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import org.jetbrains.compose.web.css.Transitions as CssTransitions


public interface Transition {
    public val cssTransitions: CssTransitions.() -> Unit
    public val hiddenStyle: StyleScope.() -> Unit
}

public fun <TElement : HTMLElement> AttrsScope<TElement>.hiddenIf(condition: Boolean, transition: Transition) {
    css {
        transitions {
            transition.cssTransitions.invoke(this)
        }
    }
    if (condition) {
        style {
            transition.hiddenStyle.invoke(this)
        }
    }
}

public fun <TElement : HTMLElement> AttrsScope<TElement>.shownIf(condition: Boolean, transition: Transition): Unit =
    hiddenIf(!condition, transition)

public object Transitions {
    public class Fade(private val duration: Duration = 300.milliseconds) : Transition {
        override val cssTransitions: CssTransitions.() -> Unit = {
            "opacity" { duration = this@Fade.duration.inWholeMilliseconds.ms }
        }

        override val hiddenStyle: StyleScope.() -> Unit = {
            opacity(0)
        }
    }

    public class Grow(private val duration: Duration = 500.milliseconds) : Transition {
        override val cssTransitions: CssTransitions.() -> Unit = {
            "opacity" { duration = this@Grow.duration.inWholeMilliseconds.ms }
            "transform" {
                duration = this@Grow.duration.inWholeMilliseconds.ms
                timingFunction = AnimationTimingFunction.cubicBezier(.15, .65, .45, 2.2)
            }
        }

        override val hiddenStyle: StyleScope.() -> Unit = {
            opacity(0)
            transform { scale(0.25) }
        }
    }

    public class FontGrow(private val duration: Duration = 300.milliseconds): Transition {
        override val cssTransitions: CssTransitions.() -> Unit = {
            "font-size" { duration = this@FontGrow.duration.inWholeMilliseconds.ms }
            "line-height" { duration = this@FontGrow.duration.inWholeMilliseconds.ms }
        }
        override val hiddenStyle: StyleScope.() -> Unit = {
            fontSize(0.em)
            lineHeight(0.em)
        }
    }

    public class Stamp(private val duration: Duration = 400.milliseconds) : Transition {
        override val cssTransitions: CssTransitions.() -> Unit = {
            "opacity" { duration = this@Stamp.duration.inWholeMilliseconds.ms }
            "transform" {
                duration = this@Stamp.duration.inWholeMilliseconds.ms
                timingFunction = AnimationTimingFunction.EaseIn
            }
        }
        override val hiddenStyle: StyleScope.() -> Unit = {
            opacity(0)
            transform { scale(1.8) }
        }
    }

}
