package net.kodein.pres

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.w3c.dom.DOMRect

public interface SlideConfig

public data class Slide(
    public val name: String? = null,
    public val stateCount: Int = 1,
    public val stateInOverview: Int? = null,
    public val width: Int = 1024,
    public val height: Int = 640,
    public val inAnimation: Animation.Set? = null,
    public val outAnimation: Animation.Set? = null,
    public val config: PresentationState.() -> Any? = { null },
    public val notes: @Composable (Int) -> Unit = {},
    public val content: @Composable (Int) -> Unit
) {
    public val lastState: Int get() = stateCount - 1
    public val rect: DOMRect = DOMRect(width = width.toDouble(), height = height.toDouble())
}

public class PresentationSlidesBuilder internal constructor() {
    internal val slides = ArrayList<Slide>()

    public operator fun Slide.unaryPlus() {
        slides += this
    }

    public operator fun Iterable<Slide>.unaryPlus() {
        slides += this
    }
}

public fun buildSlides(build: PresentationSlidesBuilder.() -> Unit): List<Slide> =
    PresentationSlidesBuilder().apply(build).slides

@Composable
public fun rememberSlides(build: PresentationSlidesBuilder.() -> Unit): List<Slide> =
    remember { buildSlides(build) }

public fun List<Slide>.animatedWith(animation: Animation.Set): List<Slide> =
    mapIndexed { index, slide ->
        slide.copy(
            inAnimation = if (index == 0) slide.inAnimation else animation,
            outAnimation = if (index == lastIndex) slide.outAnimation else animation
        )
    }
