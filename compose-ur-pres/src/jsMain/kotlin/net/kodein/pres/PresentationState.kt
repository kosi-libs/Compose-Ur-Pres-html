package net.kodein.pres

import org.w3c.dom.DOMRect
import kotlin.math.min
import kotlin.time.Duration


public data class SlideState(
    val index: Int,
    val state: Int
)

internal fun SlideState.next(slides: List<Slide>): SlideState {
    if (index > slides.lastIndex) return SlideState(slides.lastIndex, slides.last().lastState)
    if (index < 0) return SlideState(0, 0)

    val currentSlide = slides[index]
    return when {
        state != currentSlide.lastState -> SlideState(index, state + 1)
        state == currentSlide.lastState && index != slides.lastIndex -> SlideState(index + 1, 0)
        else -> this
    }
}

internal fun SlideState.prev(slides: List<Slide>): SlideState {
    if (index > slides.lastIndex) return SlideState(slides.lastIndex, slides.last().lastState)
    if (index < 0) return SlideState(0, 0)

    return when {
        state != 0 -> SlideState(index, state - 1)
        state == 0 && index != 0 -> {
            val prevSlide = slides[index - 1]
            SlideState(index - 1, prevSlide.lastState)
        }
        else -> this
    }
}

public data class PresentationState(
    val slide: SlideState,
    val slideStateCount: Int,
    val globalSlideCount: Int,
    val globalState: Int,
    val globalStateCount: Int,
    val slideAnimationDuration: Duration,
    val moveBetweenSlides: Boolean,
    val slideConfig: Any?,
    val containerSize: DOMRect?,
    val slideSize: DOMRect?,
    val slideScaleFactor: Double
)

public val PresentationState.slideLastState: Int get() = slideStateCount - 1

internal fun SlideState.presentationState(
    slides: List<Slide>,
    lastMoveWasForward: Boolean,
    defaultAnimation: Animation.Set,
    containerSize: DOMRect?,
    slideSize: DOMRect?
): PresentationState {
    val currentSlide = slides.getOrNull(index)

    return PresentationState(
        slide = this,
        slideStateCount = currentSlide?.stateCount ?: 0,
        globalSlideCount = slides.size,
        globalState = slides.subList(0, index).sumOf { it.stateCount } + state,
        globalStateCount = slides.sumOf { it.stateCount },
        slideAnimationDuration = (
                if (lastMoveWasForward) (currentSlide?.inAnimation ?: defaultAnimation).appear.duration
                else (currentSlide?.outAnimation ?: defaultAnimation).disappear.duration
                ),
        moveBetweenSlides = (lastMoveWasForward && state == 0) || (!lastMoveWasForward && state == currentSlide?.lastState),
        slideConfig = null,
        containerSize = containerSize,
        slideSize = slideSize,
        slideScaleFactor =
            if (containerSize == null || slideSize == null) 0.0
            else min((containerSize.width) / slideSize.width, (containerSize.height) / slideSize.height)
    ).let {
        it.copy(slideConfig = currentSlide?.config?.invoke(it))
    }

}