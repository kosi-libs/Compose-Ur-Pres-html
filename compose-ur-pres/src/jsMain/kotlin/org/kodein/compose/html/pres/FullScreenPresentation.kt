package org.kodein.compose.html.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import kotlinx.browser.window
import org.w3c.dom.DOMRect
import kotlin.math.abs

@Composable
internal fun FullScreenPresentation(
    presentationContainer: Container,
    slideContainer: Container,
    slides: List<Slide>,
    defaultAnimation: Animation.Set,
    presentationSize: DOMRect?,
    currentState: SlideState,
    lastMoveWasForward: Boolean
) {
    val presentationState = currentState.presentationState(
        slides,
        lastMoveWasForward,
        defaultAnimation,
        presentationSize,
        slides.getOrNull(currentState.index)?.rect
    )
    presentationState.presentationContainer(null) {
            slides.forEachIndexed { index, slide ->
                key("slide-$index") {
                    val position = when {
                        index == currentState.index -> SlidePosition.CURRENT
                        index > currentState.index -> SlidePosition.COMING
                        index < currentState.index -> SlidePosition.PASSED
                        else -> error("?")
                    }
                    SlideHandler(
                        currentState = presentationState,
                        slideContainer = slideContainer,
                        slide = slide,
                        state = when (position) {
                            SlidePosition.PASSED -> slide.lastState
                            SlidePosition.CURRENT -> currentState.state
                            SlidePosition.COMING -> 0
                        },
                        position = position,
                        defaultAnimation = defaultAnimation,
                        compose = abs(currentState.index - index) <= 1
                    )
                }
            }
    }
}
