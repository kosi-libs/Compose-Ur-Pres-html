package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import net.kodein.pres.util.transition
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLDivElement
import kotlin.math.abs
import kotlin.time.Duration

@Composable
internal fun FullScreenPresentation(
    container: PresentationContainer,
    slides: List<Slide>,
    defaultTransition: Transition.TimedSet,
    size: DOMRect?,
    currentState: PresentationState
) {
    val currentSlide = slides.getOrNull(currentState.slideIndex)

//    val slideTransitionDuration = (if (lastMoveWasForward) currentSlide?.inTransition?.duration else currentSlide?.outTransition?.duration) ?: defaultTransition.duration

//    Div({
//        classes(PresStyle.css {
//            width(100.percent)
//            height(100.percent)
//            position(Position.Absolute)
//            top(0.px)
//            left(0.px)
//            overflow("hidden")
//        })
//        containerAttrs?.invoke(this, slideTransitionDuration)
//    }) {
//        Div({
//            classes(PresStyle.css {
//                width(100.percent)
//                height(100.percent)
//                position(Position.Absolute)
//                top(0.px)
//                left(0.px)
//            })
//            if (currentSlide != null) {
//                style {
//                    transition { "background-color"(slideTransitionDuration.inWholeMilliseconds.ms) }
//                }
//            }
//            currentSlide?.containerAttrs?.invoke(this)
//        }) {

    currentState.container {
            slides.forEachIndexed { index, slide ->
                key("slide-$index") {
                    val position = when {
                        index == currentState.slideIndex -> SlidePosition.CURRENT
                        index > currentState.slideIndex -> SlidePosition.COMING
                        index < currentState.slideIndex -> SlidePosition.PASSED
                        else -> error("?")
                    }
                    SlideContainer(
                        slide = slide,
                        state = when (position) {
                            SlidePosition.PASSED -> slide.states - 1
                            SlidePosition.CURRENT -> currentState.slideState
                            SlidePosition.COMING -> 0
                        },
                        container = size,
                        position = position,
                        defaultTransition = defaultTransition,
                        compose = abs(currentState.slideIndex - index) <= 1
                    )
                }
            }
    }
//            overSlides(currentState)
//        }
//    }
}