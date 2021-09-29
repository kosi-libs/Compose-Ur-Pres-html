package net.kodein.pres

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.kodein.pres.SlidePosition.*
import net.kodein.pres.Transition.Direction.*
import net.kodein.pres.util.visibility
import net.kodein.pres.util.zIndex
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.DOMRect
import kotlin.math.min
import kotlin.time.Duration


internal enum class SlidePosition {
    PASSED,
    CURRENT,
    COMING
}

private val hide = Transition.Style { visibility("hidden") }

@Composable
internal fun SlideContainer(
    slide: Slide,
    state: Int,
    container: DOMRect?,
    position: SlidePosition,
    defaultTransition: Transition.TimedSet,
    compose: Boolean
) {
    var previousState: SlidePosition? by remember { mutableStateOf(null) }
    var transitionStyle: TransitionStyle? by remember { mutableStateOf(null) }
    var transitioning by remember { mutableStateOf(false) }

    LaunchedEffect(position) {
        suspend fun executeTransition(t: Transition, duration: Duration, direction: Transition.Direction) {
            transitioning = true
            t.execute(duration, direction) { transitionStyle = it }
            transitioning = false
        }

        val pState = previousState
        val nState = position
        launch {
            when (pState to nState) {
                CURRENT to PASSED -> executeTransition((slide.outTransition ?: defaultTransition).transitions.disappear, slide.outTransition?.duration ?: defaultTransition.duration, FORWARD)
                CURRENT to COMING -> executeTransition((slide.inTransition ?: defaultTransition).transitions.disappear, slide.inTransition?.duration ?: defaultTransition.duration, BACKWARD)
                PASSED to CURRENT -> executeTransition((slide.outTransition ?: defaultTransition).transitions.appear, slide.outTransition?.duration ?: defaultTransition.duration, BACKWARD)
                COMING to CURRENT -> executeTransition((slide.inTransition ?: defaultTransition).transitions.appear, slide.inTransition?.duration ?: defaultTransition.duration, FORWARD)
            }

            transitionStyle = when (nState) {
                PASSED -> (slide.outTransition ?: defaultTransition).transitions.appear.prepare(BACKWARD) + hide
                CURRENT -> null
                COMING -> (slide.inTransition ?: defaultTransition).transitions.appear.prepare(FORWARD) + hide
            }
        }
        previousState = position
    }

    if (!compose && !transitioning) return

    Div({
        classes(PresStyle.css {
            position(Position.Absolute)
            width(slide.width.px)
            height(slide.height.px)
            zIndex(if (position == CURRENT) 2 else 1)
        })
        style {
            if (container != null) {
                left(((container.width - slide.width) / 2).px)
                top(((container.height - slide.height) / 2).px)

                val factor = min(container.width / slide.width, container.height / slide.height)
                transform { scale(factor) }
            }
        }

    }) {
        Div({
            classes(PresStyle.css {
                width(100.percent)
                height(100.percent)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
            }, "slide")
            style {
                transitionStyle?.invoke(this)
            }
        }) {
            slide.content(state)
        }
    }
}
