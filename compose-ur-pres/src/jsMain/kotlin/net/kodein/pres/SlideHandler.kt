package net.kodein.pres

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.kodein.pres.Animation.Direction.BACKWARD
import net.kodein.pres.Animation.Direction.FORWARD
import net.kodein.pres.SlidePosition.*
import net.kodein.pres.util.Style
import net.kodein.pres.util.Visibility
import net.kodein.pres.util.visibility
import net.kodein.pres.util.zIndex
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.kodein.cic.css


internal enum class SlidePosition {
    PASSED,
    CURRENT,
    COMING
}

private val hide = Animation.Style { visibility(Visibility.Hidden) }

@Composable
internal fun SlideHandler(
    currentState: PresentationState,
    slideContainer: Container,
    slide: Slide,
    state: Int,
    position: SlidePosition,
    defaultAnimation: Animation.Set,
    compose: Boolean
) {
    fun getAnimation(p: SlidePosition) = when (p) {
        PASSED -> (slide.outAnimation ?: defaultAnimation).appear.prepare(BACKWARD) + hide
        CURRENT -> null
        COMING -> (slide.inAnimation ?: defaultAnimation).appear.prepare(FORWARD) + hide
    }

    var previousState: SlidePosition? by remember { mutableStateOf(null) }
    var animationStyle: Style? by remember { mutableStateOf(getAnimation(position)) }
    var animating by remember { mutableStateOf(false) }

    LaunchedEffect(position) {
        suspend fun executeAnimation(t: Animation, direction: Animation.Direction) {
            animating = true
            t.execute(direction) { animationStyle = it }
            animating = false
        }

        val pState = previousState
        val nState = position
        launch {
            when (pState to nState) {
                CURRENT to PASSED -> executeAnimation((slide.outAnimation ?: defaultAnimation).disappear, FORWARD)
                CURRENT to COMING -> executeAnimation((slide.inAnimation ?: defaultAnimation).disappear, BACKWARD)
                PASSED to CURRENT -> executeAnimation((slide.outAnimation ?: defaultAnimation).appear, BACKWARD)
                COMING to CURRENT -> executeAnimation((slide.inAnimation ?: defaultAnimation).appear, FORWARD)
            }

            animationStyle = getAnimation(nState)
        }
        previousState = position
    }

    if (!compose && !animating) return

    Div({
        css {
            position(Position.Absolute)
            width(slide.width.px)
            height(slide.height.px)
            zIndex(if (position == CURRENT) 2 else 1)
        }
        style {
            if (currentState.containerSize != null) {
                left(((currentState.containerSize.width - slide.width) / 2).px)
                top(((currentState.containerSize.height - slide.height) / 2).px)

                transform { scale(currentState.slideScaleFactor) }
            }
        }

    }) {
        Div({
            classes("slide")
            css {
                width(100.percent)
                height(100.percent)
            }
            style {
                animationStyle?.invoke(this)
            }
        }) {
            currentState.slideContainer {
                slide.content(state)
            }
        }
    }
}
