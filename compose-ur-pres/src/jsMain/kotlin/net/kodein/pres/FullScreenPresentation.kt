package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import org.w3c.dom.DOMRect
import kotlin.math.abs

@Composable
internal fun FullScreenPresentation(
    presentationContainer: Container,
    slideContainer: Container,
    slides: List<Slide>,
    defaultAnimation: Animation.Set,
    presentationSize: DOMRect?,
    currentState: PresentationState
) {
    currentState.presentationContainer {
            slides.forEachIndexed { index, slide ->
                key("slide-$index") {
                    val position = when {
                        index == currentState.slideIndex -> SlidePosition.CURRENT
                        index > currentState.slideIndex -> SlidePosition.COMING
                        index < currentState.slideIndex -> SlidePosition.PASSED
                        else -> error("?")
                    }
                    SlideHandler(
                        currentState = currentState,
                        slideContainer = slideContainer,
                        slide = slide,
                        state = when (position) {
                            SlidePosition.PASSED -> slide.states - 1
                            SlidePosition.CURRENT -> currentState.slideState
                            SlidePosition.COMING -> 0
                        },
                        presentationSize = presentationSize,
                        position = position,
                        defaultAnimation = defaultAnimation,
                        compose = abs(currentState.slideIndex - index) <= 1
                    )
                }
            }
    }
}
